package com.example.pomodoro.data

// --- PomodoroController.kt ---
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex

sealed class PomodoroState {
    object Idle : PomodoroState()
    data class Studying(val remaining: Int, val session: Int) : PomodoroState()
    data class Resting(val remaining: Int, val session: Int) : PomodoroState()
    object Paused : PomodoroState()
    object Finished : PomodoroState()
    data class Error(val message: String) : PomodoroState()
}



interface PomodoroController {
    val pomodoroState: StateFlow<PomodoroState>
    val focusUiState: StateFlow<FocusUiState>
    val restUiState: StateFlow<RestUiState>

    fun configure(studyMinutes: Int, restMinutes: Int, sessions: Int)
    fun start()
    fun pause()
    fun resume()
    fun reset()
    fun skipRest()
    fun formatter(durationSeconds: Int): String
}


class PomodoroControllerImpl(
    private val scope: CoroutineScope
) : PomodoroController {

    private val _pomodoroState = MutableStateFlow<PomodoroState>(PomodoroState.Idle)
    override val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    private val _focusUiState = MutableStateFlow(FocusUiState())
    override val focusUiState: StateFlow<FocusUiState> = _focusUiState.asStateFlow()

    private val _restUiState = MutableStateFlow(RestUiState())
    override val restUiState: StateFlow<RestUiState> = _restUiState.asStateFlow()

    private var studyDurationSec: Int = 0
    private var restDurationSec: Int = 0
    private var totalSessions: Int = 0
    private var currentSession: Int = 0

    private var timerJob: Job? = null

    override fun configure(studyMinutes: Int, restMinutes: Int, sessions: Int) {
        studyDurationSec = studyMinutes * 60
        restDurationSec = restMinutes * 60
        totalSessions = sessions
        currentSession = 1

        _focusUiState.value = FocusUiState(
            duration = studyDurationSec,
            initialDuration = studyDurationSec,
            sessions = sessions,
            initialSessions = sessions
        )
        _restUiState.value = RestUiState(
            restDuration = restDurationSec,
            initialRestDuration = restDurationSec
        )
        _pomodoroState.value = PomodoroState.Idle
    }

    override fun start() {
        timerJob?.cancel()
        timerJob = scope.launch {
            runSessionLoop()
        }
    }

    private suspend fun runSessionLoop() {
        for (session in currentSession..totalSessions) {
            // Study phase
            setState(PomodoroState.Studying(studyDurationSec, session))
            runTimer(studyDurationSec) { remaining ->
                setState(PomodoroState.Studying(remaining, session))
            }

            if (session == totalSessions) {
                setState(PomodoroState.Finished)
                return
            }

            // Rest phase
            setState(PomodoroState.Resting(restDurationSec, session))
            runTimer(restDurationSec) { remaining ->
                setState(PomodoroState.Resting(remaining, session))
            }
        }
    }

    private suspend fun runTimer(duration: Int, onTick: (Int) -> Unit) {
        var timeLeft = duration
        while (timeLeft > 0 && _pomodoroState.value !is PomodoroState.Idle) {
            if (_pomodoroState.value is PomodoroState.Paused) {
                delay(200L)
                continue
            }
            delay(1000L)
            timeLeft--
            onTick(timeLeft)
        }
    }

    override fun pause() {
        _pomodoroState.value = PomodoroState.Paused
    }

    override fun resume() {
        // restore last known state
        val prev = _pomodoroState.value
        when (prev) {
            is PomodoroState.Studying ->
                _pomodoroState.value = PomodoroState.Studying(prev.remaining, prev.session)
            is PomodoroState.Resting ->
                _pomodoroState.value = PomodoroState.Resting(prev.remaining, prev.session)
            else -> Unit
        }
    }

    override fun reset() {
        timerJob?.cancel()
        _pomodoroState.value = PomodoroState.Idle
        _focusUiState.update { it.copy(duration = it.initialDuration, sessions = it.initialSessions, isRunning = false) }
        _restUiState.update { it.copy(restDuration = it.initialRestDuration, isStudying = false, isShowingMenu = true) }
    }

    override fun skipRest() {
        if (_pomodoroState.value is PomodoroState.Resting) {
            _pomodoroState.value = PomodoroState.Studying(studyDurationSec, currentSession + 1)
        }
    }

    override fun formatter(durationSeconds: Int): String {
        val m = durationSeconds / 60
        val s = durationSeconds % 60
        return String.format("%02d:%02d", m, s)
    }

    private fun setState(state: PomodoroState) {
        _pomodoroState.value = state

        // Derive UI states automatically
        when (state) {
            is PomodoroState.Studying -> {
                _focusUiState.update {
                    it.copy(duration = state.remaining, isRunning = true, sessions = totalSessions - state.session + 1)
                }
                _restUiState.update { it.copy(isStudying = true) }
            }
            is PomodoroState.Resting -> {
                _restUiState.update { it.copy(restDuration = state.remaining, isStudying = false) }
            }
            PomodoroState.Idle -> {
                _focusUiState.update { it.copy(isRunning = false) }
                _restUiState.update { it.copy(isShowingMenu = true) }
            }
            PomodoroState.Finished -> {
                _focusUiState.update { it.copy(isRunning = false, duration = 0, sessions = 0) }
                _restUiState.update { it.copy(isStudying = false, restDuration = 0) }
            }
            else -> Unit
        }
    }
}

