package com.example.pomodoro.data

// --- PomodoroController.kt ---
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
    fun giveUp()
    suspend fun stop() // optional helper for tests
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

    override fun setSessions(sessions: Int) {
        _focusUiState.update { it.copy(duration = sessions) }
    }

    override fun start() {
        // Cancel previous jobs (if any)
        studyJob?.cancel()
        restJob?.cancel()

        //set isRunning to true, isStudying to true
        _focusUiState.update { it.copy(isRunning = true) }
        _restUiState.update { it.copy(isStudying = true) }

        // Launch study countdown
        studyJob = scope.launch {
            try {
                countdownStudy()
                // Switch into rest if restDuration > 0 for now/ but have to add user setting later
                _focusUiState.update { it.copy( isRunning = true) }
                _restUiState.update { it.copy(isStudying = true) }

                if (restUiState.value.restDuration > 0) {
                    restJob = launch { countdownRest() }
                    restJob?.join()
                }
            } catch (e: CancellationException) {
                // job cancelled — leave state as-is or reset as desired
            } finally {
                // Ensure we mark stopped when finished
                _focusUiState.update { it.copy( isRunning = true) }
                _restUiState.update { it.copy(isStudying = true) }
            }
        }
    }

    private suspend fun countdownStudy() = coroutineScope {
        while (isActive) {
            val current = focusUiState.value
            if (!current.isRunning || current.duration <= 0) break
            delay(1000L)
            _focusUiState.update { it.copy(duration = (it.duration - 1).coerceAtLeast(0)) }
        }
    } //countdown for study session

    private suspend fun countdownRest() = coroutineScope {
        while (isActive) {
            val currentRest = _restUiState.value
            val currentFocus = _focusUiState.value
            if (!currentFocus.isRunning || currentRest.isStudying || currentRest.restDuration <= 0) break // if isRunning = true or isStudying = true or restDuration <= 0 then break
            delay(1000L)
            _restUiState.update { it.copy(restDuration = (it.restDuration - 1).coerceAtLeast(0)) }
        }

    } //countdown for rest session

    override fun giveUp() {
        studyJob?.cancel()
        restJob?.cancel()
        _focusUiState.update {
            it.copy(
                isRunning = false,
                duration = it.initialDuration,
            )
        }

        _restUiState.update {
            it.copy(
                isStudying = false,
                restDuration = it.initialRestDuration,
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
