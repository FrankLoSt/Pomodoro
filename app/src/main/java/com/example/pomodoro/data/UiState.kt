package com.example.pomodoro.data

// --- UiState.kt ---

enum class AppPhase {IDLE, FOCUSING, RESTING, FINISHED}
enum class TimerState {RUNNING, PAUSED, STOPPED}


data class FocusUiState(
    // SET UP
    val listFocusDuration: List<Int> = (5..180 step 5).toList(),
    val listSessions: List<Int> = (1..30 step 1).toList(),
    val duration: Int = 10, //test with 10s = > duration is calculated in seconds, not in minutes.
    val initialDuration: Int = 10,//test with 10
    val sessions: Int = 1,
    val totalSessions: Int = 1, //test with 1
    //NEW STATE CONTROL
    val appPhrase: AppPhase = AppPhase.FINISHED,
    val timerState: TimerState = TimerState.STOPPED
) {
    // Helpers
    fun studyProgress(): Float =
        if (initialDuration > 0) 1f - duration.toFloat() / initialDuration else 0f
}

data class RestUiState(
    val listRestDuration: List<Int> = listOf(1) + (5..30 step 5).toList(),
    val restDuration: Int = 5, //test with 30
    val initialRestDuration: Int = 5,//test with 30
) {
    fun restProgress(): Float = if (initialRestDuration > 0) 1f - restDuration.toFloat() / initialRestDuration else 0f
}

data class InitSetUpState(
    val toggleSetUp: Boolean = false,
)