package com.example.pomodoro.data

// --- PomodoroController.kt ---
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.pomodoro.data.datastore.SettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

interface PomodoroController {
    val focusUiState: StateFlow<FocusUiState>

    val restUiState: StateFlow<RestUiState>


    fun setDurationMinutes(minutes: Int)
    fun setRestDurationMinutes(minutes: Int)

    fun setSessions(sessions: Int)

    fun start()
    fun breakFun()
    fun pause() // optional helper for tests\
    fun resume ()

    fun reset()
    fun formatter(durationSeconds: Int): String
}

class PomodoroControllerImpl @Inject constructor(
    private val scope: CoroutineScope, // usually viewModelScope,
    private val settingsRepository: SettingsRepository
) : PomodoroController {

    private val _focusUiState = MutableStateFlow(FocusUiState())
    override val focusUiState: StateFlow<FocusUiState> = _focusUiState.asStateFlow()

    private val _restUiState = MutableStateFlow(RestUiState())

    override val restUiState: StateFlow<RestUiState> = _restUiState.asStateFlow()



    //Create job controllers for 2 countdown
    private var studyJob: Job? = null

    //private var restJob: Job? = null //-> I leave it here for future use

    // var pauseJob: Job? = null //-> I leave it here for future use

    private val mutex = Mutex() // protect state if necessary

    override fun setDurationMinutes(minutes: Int) {
        val seconds = minutes   //remove  * 60 for testing  - minutes * 60
        _focusUiState.update { it.copy(duration = seconds, initialDuration = seconds) }
        Log.d("DEBUG", "setDurationMinutes: $seconds assigned")
    }

    override fun setRestDurationMinutes(minutes: Int) {
        val seconds = minutes  //remove  * 60 for testing  - minutes * 60
        _restUiState.update { it.copy(restDuration = seconds, initialRestDuration = seconds) }
        Log.d("DEBUG", "setRestDurationMinutes: $seconds assigned")
    }

    override fun setSessions(sessions: Int) {
        _focusUiState.update { it.copy(totalSessions = sessions) } //initial session = 1, it will increase by 1 when studying
        Log.d("DEBUG", "setSessions: $sessions assigned")
    }

    var totalFocusSeconds = 0

   suspend fun saveTotalFocusMinutes() {
        settingsRepository.incrementFocusSeconds(1)
       Log.d("DEBUG", "ACTUAL saveTotalFocusMinutes: ${settingsRepository.getTotalFocusMinutes()} saved")
   }
    private suspend fun countdownStudy() = coroutineScope {
        while (isActive) {
            while (focusUiState.value.isPause) {
                Log.d("DEBUG", "countdownStudy: pausing")
                delay(100L)
            }
            val current = focusUiState.value

            if (!current.isRunning || current.duration <= 0) break //if isRunning = false or duration <= 0 then break
            delay(1000L)

            _focusUiState.update { it.copy(duration = if (focusUiState.value.isPause) it.duration else (it.duration - 1).coerceAtLeast(0)) }
            saveTotalFocusMinutes() //save total focus time every second when studying

            Log.d("DEBUG", "countdownStudy: ${current.duration}")
        }
        Log.d("DEBUG", "countdownStudy: study finished")
    } //countdown for study session




    private suspend fun countdownRest() = coroutineScope {
        while (isActive) {
            while (focusUiState.value.isPause) {
                Log.d("DEBUG", "countdownRest: pausing")
                delay(100L)
            }
            Log.d("DEBUG", " test totalFcous: $totalFocusSeconds")
            val currentRest = _restUiState.value
            if (currentRest.isStudying || currentRest.restDuration <= 0) break // if isRunning = true or isStudying = true or restDuration <= 0 then break
            delay(1000L)

            _restUiState.update { it.copy(restDuration = if (focusUiState.value.isPause) it.restDuration else (it.restDuration - 1).coerceAtLeast(0)) }
                                                       //if isPause -> no update
            Log.d("DEBUG", "countdownRest: ${currentRest.restDuration}")
        }
        Log.d("DEBUG", "countdownRest: break finished")
    } //countdown for rest session

    override fun start () {
        studyJob?.cancel()

        Log.d("DEBUG", "start: start() runs")
        _restUiState.update { it.copy(isShowingMenu = false)}
        studyJob = scope.launch {
            while (
               focusUiState.value.sessions <= focusUiState.value.totalSessions
            ) {
                _focusUiState.update{ it.copy(isRunning = true, duration = it.initialDuration) }
                _restUiState.update { it.copy(isStudying = true) }
                countdownStudy() //before running countdown, make sure isRunning = true, duration > 0, and isStudying = true.

                if(focusUiState.value.sessions == focusUiState.value.totalSessions) break // at the last session, break

                _restUiState.update { it.copy(isStudying = false, restDuration = it.initialRestDuration) } //not studying anymore
                countdownRest() // isStudying = false because users are not studying, they are taking a break.

                _focusUiState.update {
                    it.copy(
                        sessions = it.sessions + 1, //
                    )
                } // after finish studying and resting => session + 1 and start studying again

                Log.d("DEBUG", "Number of sessions: ${focusUiState.value.sessions}/${focusUiState.value.totalSessions}")
            }

            _focusUiState.update { it.copy(isFinished = true) } //isFinished = true => display alert dialog, users have to click Ok to call toggleisFinished() to close it.

            reset()
            Log.d("DEBUG", "start: start() ends")
        }
    }

     override  fun reset(){
        studyJob?.cancel()

        _focusUiState.update {
            it.copy(
                isRunning = false,
                duration = it.initialDuration,
                sessions = 1,
                isPause = false,
            )
        }
        _restUiState.update {
            it.copy(
                isStudying = false,
                restDuration = it.initialRestDuration,
                isShowingMenu = true
            )
        }
        Log.d("reset", "reset: reset done!")
    }
    fun toggleisFinished () {
        _focusUiState.update { it.copy(isFinished = false) }
    }

    // still need breakFun because when my app scale, I need to save users data
    //Before cancell everything -> save users focus time.
    override fun breakFun () {
        Log.d("DEBUG", "breakFun: breakFun() runs")
        reset()
    } //cancel all jobs


    override fun pause() {
        Log.d("DEBUG", "pause: pause() runs")
        _focusUiState.update { it.copy(isPause = true) }

    }
    override fun resume () {
        Log.d("DEBUG", "resume: resume() runs")

        _focusUiState.update { it.copy(isPause = false) }
    }


    override fun formatter(durationSeconds: Int): String {
        val m = durationSeconds / 60
        val s = durationSeconds % 60
        return String.format("%02d:%02d", m, s)
    }
}
