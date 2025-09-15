package com.example.pomodoro.data

// --- PomodoroController.kt ---
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex

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

class PomodoroControllerImpl(
    private val scope: CoroutineScope // usually viewModelScope,
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
        _focusUiState.update { it.copy(sessions = sessions, initialSessions = sessions) }
        Log.d("DEBUG", "setSessions: $sessions assigned")
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
            _focusUiState.update { it.copy(duration = (it.duration - 1).coerceAtLeast(0)) }
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
            val currentRest = _restUiState.value

            if (currentRest.isStudying || currentRest.restDuration <= 0) break // if isRunning = true or isStudying = true or restDuration <= 0 then break
            delay(1000L)
            _restUiState.update { it.copy(restDuration = (it.restDuration - 1).coerceAtLeast(0)) }
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
                focusUiState.value.sessions > 0
            ) {
                _focusUiState.update{ it.copy(isRunning = true, duration = it.initialDuration) }
                _restUiState.update { it.copy(isStudying = true) }
                countdownStudy()

                if(focusUiState.value.sessions == 1) break

                _restUiState.update { it.copy(isStudying = false, restDuration = it.initialRestDuration) } //not studying anymore
                countdownRest() // isRunning, isStudying = false

                _focusUiState.update {
                    it.copy(
                        sessions = (it.sessions - 1).coerceAtLeast(0)
                    )
                } // -1 session after studying, resting
                Log.d("DEBUG", "Number of sessions: ${focusUiState.value.sessions}/${focusUiState.value.initialSessions}")
            }
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
                sessions = it.initialSessions,
                isPause = false
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

    // still need breakFun because when my app scale, I need to save users data
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
