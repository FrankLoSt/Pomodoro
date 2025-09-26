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
    }

    override fun setRestDurationMinutes(minutes: Int) {
        val seconds = minutes // * 60 in prod
        _restUiState.update {
            it.copy(duration = seconds, initialDuration = seconds)
        }
    }

    override fun setSessions(sessions: Int) {
        _focusUiState.update { it.copy(totalSessions = sessions) }
    }

    override fun start() {
        sessionJob?.cancel()
        sessionJob = scope.launch {
            _focusUiState.update {
                it.copy(
                    focusPhase = PomodoroPhase.FOCUS,
                    focusTimerStatus = TimerStatus.RUNNING,
                    duration = it.initialDuration
                )
            }

            while (focusUiState.value.currentSession <= focusUiState.value.totalSessions) {
                runFocusPhase()
                if (focusUiState.value.currentSession == focusUiState.value.totalSessions) break
                //break if the user is on the last session
                runRestPhase()
                _focusUiState.update {
                    it.copy(currentSession = it.currentSession + 1)
                }
            }

            _focusUiState.update {
                it.copy(
                    focusPhase = PomodoroPhase.FINISHED,
                    focusTimerStatus = TimerStatus.STOPPED)
            }
            reset()
        }
    }

    private suspend fun runFocusPhase() {
        _focusUiState.update { it.copy(focusPhase = PomodoroPhase.FOCUS) }
        while (focusUiState.value.duration > 0 && focusUiState.value.focusTimerStatus == TimerStatus.RUNNING) {
            delay(1000L)

            if (focusUiState.value.focusTimerStatus == TimerStatus.PAUSED) continue

            _focusUiState.update { it.copy(duration = (it.duration - 1).coerceAtLeast(0)) }
            settingsRepository.saveHourlyFocusDuration(1)
        }
        _focusUiState.update {
            it.copy(focusPhase = PomodoroPhase.IDLE, focusTimerStatus = TimerStatus.STOPPED)
        }
    }

    private suspend fun runRestPhase() {
        _restUiState.update {
            it.copy(
                restPhase = PomodoroPhase.REST,
                restTimerStatus = TimerStatus.RUNNING, duration = it.initialDuration)
        }

        while (_restUiState.value.duration > 0 && _restUiState.value.restTimerStatus == TimerStatus.RUNNING) {
            delay(1000L)

            if (_restUiState.value.restTimerStatus == TimerStatus.PAUSED) continue

            _restUiState.update {
                it.copy(duration = (it.duration - 1).coerceAtLeast(0))
            }
            Log.d("DEBUG", "Rest duration: ${restUiState.value.duration}")
        }

        _restUiState.update {
            it.copy(restPhase = PomodoroPhase.IDLE, restTimerStatus = TimerStatus.STOPPED)
        }
    }

    override fun pause() {
        if (focusUiState.value.focusPhase == PomodoroPhase.FOCUS) {
            _focusUiState.update { it.copy(focusTimerStatus = TimerStatus.PAUSED) }
        } else {
            _restUiState.update { it.copy(restTimerStatus = TimerStatus.PAUSED) }
        }

    }

    override fun resume() {
        if (focusUiState.value.focusPhase == PomodoroPhase.FOCUS) {
            _focusUiState.update { it.copy(focusTimerStatus = TimerStatus.RUNNING) }
        } else {
            _restUiState.update { it.copy(restTimerStatus = TimerStatus.RUNNING) }
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

