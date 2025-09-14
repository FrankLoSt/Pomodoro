package com.example.pomodoro

// --- PomodoroController.kt ---
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex

interface PomodoroController {
    val focusUIiState: StateFlow<FocusUiState>

    val restUIiState: StateFlow<RestUiState>


    fun setDurationMinutes(minutes: Int)
    fun setRestDurationMinutes(minutes: Int)
    fun start()
    fun giveUp()
    suspend fun stop() // optional helper for tests
    fun formatter(durationSeconds: Int): String
}

class PomodoroControllerImpl(
    FocusUiState: FocusUiState = FocusUiState(),
    RestUiState: RestUiState = RestUiState(),
    private val scope: CoroutineScope // usually viewModelScope
) : PomodoroController {

    private val _focusUiState = MutableStateFlow(FocusUiState)
    val focusUiState: StateFlow<FocusUiState> = _focusUiState.asStateFlow()

    private val _restUiState = MutableStateFlow(RestUiState)

    val restUiState: StateFlow<RestUiState> = _restUiState.asStateFlow()

    private var studyJob: Job? = null
    private var restJob: Job? = null

    private val mutex = Mutex() // protect state if necessary

    override fun setDurationMinutes(minutes: Int) {
        val seconds = minutes * 60
        _focusUiState.update { it.copy(duration = seconds, initialDuration = seconds) }
    }

    override fun setRestDurationMinutes(minutes: Int) {
        val seconds = minutes * 60
        _restUiState.update { it.copy(restDuration = seconds, initialRestDuration = seconds) }
    }

    override fun start() {
        // Cancel previous jobs (if any)
        studyJob?.cancel()
        restJob?.cancel()

        _focusUiState.update { it.copy(isRunning = true) }
        _restUiState.update { it.copy(isStudying = true) }

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
    } //countdown for study session

    private suspend fun countdownRest() = coroutineScope {
        while (isActive) {
            val current = _uiState.value
            if (!current.isRunning || current.isStudying || current.restDuration <= 0) break
            delay(1000L)
            _uiState.update { it.copy(restDuration = (it.restDuration - 1).coerceAtLeast(0)) }
        }
    } //countdown for rest session

    override fun giveUp() {
        studyJob?.cancel()
        restJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                isStudying = false,  //potential bug
                // reset durations to initial settings so UI shows initial values
                duration = it.initialDuration,
                restDuration = it.initialRestDuration
            )
        }
    } //cancel all jobs

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
