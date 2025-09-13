package com.example.pomodoro

// --- PomodoroController.kt ---
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex

interface PomodoroController {
    val uiState: StateFlow<UiState>

    fun setDurationMinutes(minutes: Int)
    fun setRestDurationMinutes(minutes: Int)
    fun start()
    fun giveUp()
    suspend fun stop() // optional helper for tests
    fun formatter(durationSeconds: Int): String
}

class PomodoroControllerImpl(
    initialState: UiState = UiState(),
    private val scope: CoroutineScope // usually viewModelScope
) : PomodoroController {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var studyJob: Job? = null
    private var restJob: Job? = null

    private val mutex = Mutex() // protect state if necessary

    override fun setDurationMinutes(minutes: Int) {
        val seconds = minutes * 60
        _uiState.update { it.copy(duration = seconds, initialDuration = seconds) }
    }

    override fun setRestDurationMinutes(minutes: Int) {
        val seconds = minutes * 60
        _uiState.update { it.copy(restDuration = seconds, initialRestDuration = seconds) }
    }

    override fun start() {
        // Cancel previous jobs (if any)
        studyJob?.cancel()
        restJob?.cancel()

        _uiState.update { it.copy(isRunning = true, isStudying = true) }

        // Launch study countdown
        studyJob = scope.launch {
            try {
                countdownStudy()
                // Switch into rest if restDuration > 0
                _uiState.update { it.copy(isStudying = false, isRunning = true) }
                if (_uiState.value.restDuration > 0) {
                    restJob = launch { countdownRest() }
                    restJob?.join()
                }
            } catch (e: CancellationException) {
                // job cancelled — leave state as-is or reset as desired
            } finally {
                // Ensure we mark stopped when finished
                _uiState.update { it.copy(isRunning = false, isStudying = false) }
            }
        }
    }

    private suspend fun countdownStudy() = coroutineScope {
        while (isActive) {
            val current = _uiState.value
            if (!current.isRunning || current.duration <= 0) break
            delay(1000L)
            _uiState.update { it.copy(duration = (it.duration - 1).coerceAtLeast(0)) }
        }
    }

    private suspend fun countdownRest() = coroutineScope {
        while (isActive) {
            val current = _uiState.value
            if (!current.isRunning || current.isStudying || current.restDuration <= 0) break
            delay(1000L)
            _uiState.update { it.copy(restDuration = (it.restDuration - 1).coerceAtLeast(0)) }
        }
    }

    override fun giveUp() {
        studyJob?.cancel()
        restJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                isStudying = false,
                // reset durations to initial settings so UI shows initial values
                duration = it.initialDuration,
                restDuration = it.initialRestDuration
            )
        }
    }

    override suspend fun stop() {
        giveUp()
        studyJob?.join()
        restJob?.join()
    }

    override fun formatter(durationSeconds: Int): String {
        val m = durationSeconds / 60
        val s = durationSeconds % 60
        return String.format("%02d:%02d", m, s)
    }
}
