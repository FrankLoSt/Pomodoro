package com.example.pomodoro.data

// --- PomodoroController.kt ---
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.pomodoro.data.datastore.SettingsRepository
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
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

@RequiresApi(Build.VERSION_CODES.O)
class PomodoroControllerImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope,
) : PomodoroController {

    private val _focusUiState = MutableStateFlow(FocusUiState())
    override val focusUiState: StateFlow<FocusUiState> = _focusUiState.asStateFlow()

    private val _restUiState = MutableStateFlow(RestUiState())
    override val restUiState: StateFlow<RestUiState> = _restUiState.asStateFlow()

    private var sessionJob: Job? = null

    override fun setDurationMinutes(minutes: Int) {
        val seconds = minutes // * 60 in prod
        _focusUiState.update {
            it.copy(duration = seconds, initialDuration = seconds)
        }
        Log.d("DEBUG", "Focus Duration: ${focusUiState.value.duration}")
    }

    override fun setRestDurationMinutes(minutes: Int) {
        val seconds = minutes // * 60 in prod
        _restUiState.update {
            it.copy(duration = seconds, initialDuration = seconds)
        }
        Log.d("DEBUG", "Rest Duration: ${restUiState.value.duration}")
    }

    override fun setSessions(sessions: Int) {
        _focusUiState.update { it.copy(totalSessions = sessions) }
        Log.d("DEBUG", "Sessions: ${focusUiState.value.totalSessions}")
    }

    override fun start() {
        Log.d("DEBUG", "Start(): Start")
        sessionJob?.cancel()
        sessionJob = scope.launch {
            while (focusUiState.value.currentSession <= focusUiState.value.totalSessions) {
                runFocusPhase()
                if (focusUiState.value.currentSession == focusUiState.value.totalSessions) break
                //break if the user is on the last session
                runRestPhase()
                _focusUiState.update {
                    it.copy(currentSession = it.currentSession + 1)
                }
            }
            _focusUiState.update { it.copy(focusPhase = PomodoroPhase.FINISHED) }
            reset()
        }
    }

    private suspend fun runFocusPhase() {
        _focusUiState.update { it.copy(focusTimerStatus = TimerStatus.RUNNING,focusPhase = PomodoroPhase.FOCUS) }
        _restUiState.update { it.copy(restTimerStatus = TimerStatus.STOPPED,restPhase = PomodoroPhase.IDLE) }
        while (focusUiState.value.duration > 0  && focusUiState.value.focusPhase == PomodoroPhase.FOCUS) {
            while (focusUiState.value.focusTimerStatus == TimerStatus.PAUSED) {
                delay(100L)
                Log.d("DEBUG", "Focus duration: PAUSED")
                continue
            }

            delay(1000L)

            _focusUiState.update { it.copy(duration = if(focusUiState.value.focusTimerStatus == TimerStatus.PAUSED) (it.duration) else (it.duration - 1).coerceAtLeast(0)) }
            settingsRepository.saveHourlyFocusDuration(1)
        }
        Log.d("DEBUG", "Focus duration: DONE!")
    }

    private suspend fun runRestPhase() {
        _focusUiState.update { it.copy(focusTimerStatus = TimerStatus.STOPPED,focusPhase = PomodoroPhase.IDLE) }
        _restUiState.update {
            it.copy(
                restPhase = PomodoroPhase.REST,
                restTimerStatus = TimerStatus.RUNNING,
            )
        }

        while (restUiState.value.duration > 0 && restUiState.value.restPhase == PomodoroPhase.REST) {

            while (restUiState.value.restTimerStatus == TimerStatus.PAUSED) {
                delay(100L)
                Log.d("DEBUG", "Rest duration: PAUSED")
                continue
            }

            delay(1000L)

            _restUiState.update {
                it.copy(duration = if(restUiState.value.restTimerStatus == TimerStatus.PAUSED) (it.duration) else (it.duration - 1).coerceAtLeast(0))
            }
            Log.d("DEBUG", "Rest duration: ${restUiState.value.duration}")
        }

        _restUiState.update {
            it.copy(restPhase = PomodoroPhase.IDLE, restTimerStatus = TimerStatus.STOPPED)
        }
        Log.d("DEBUG", "Rest duration: DONE!")
    }

    override fun pause() {
        if (focusUiState.value.focusPhase == PomodoroPhase.FOCUS) {
            _focusUiState.update { it.copy(focusTimerStatus = TimerStatus.PAUSED) }
            Log.d("DEBUG", "Pause(): Pause focus ")
        } else if (restUiState.value.restPhase == PomodoroPhase.REST) {
            _restUiState.update { it.copy(restTimerStatus = TimerStatus.PAUSED) }
            Log.d("DEBUG", "Pause(): Pause rest ")
        }
    }

    override fun resume() {
        if (focusUiState.value.focusPhase == PomodoroPhase.FOCUS) {
            _focusUiState.update { it.copy(focusTimerStatus = TimerStatus.RUNNING) }
            Log.d("DEBUG", "Resume(): Resume focus ")
        } else if (restUiState.value.restPhase == PomodoroPhase.REST) {
            _restUiState.update { it.copy(restTimerStatus = TimerStatus.RUNNING) }
            Log.d("DEBUG", "Resume(): Resume rest ")
        }
    }

    override fun reset() {
        sessionJob?.cancel()
        _focusUiState.update {
            it.copy(
                duration = it.initialDuration,
                currentSession = 1,
                focusTimerStatus = TimerStatus.STOPPED,
                focusPhase = PomodoroPhase.IDLE
            )
        }
        _restUiState.update {
            it.copy(
                duration = it.initialDuration,
                restTimerStatus = TimerStatus.STOPPED,
                restPhase = PomodoroPhase.IDLE
            )
        }
    }

    override fun breakFun() {
        reset()
    }

    override fun formatter(durationSeconds: Int): String {
        val m = durationSeconds / 60
        val s = durationSeconds % 60
        return String.format("%02d:%02d", m, s)
    }
}

