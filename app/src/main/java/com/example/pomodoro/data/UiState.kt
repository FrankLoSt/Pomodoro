package com.example.pomodoro.data

// --- UiState.kt ---
// Shared constants for defaults
object PomodoroDefaults {
    val focusDurations = (5..180 step 5).toList() // in minutes
    val restDurations = listOf(1) + (5..30 step 5).toList()
    val sessions = (1..30).toList()
    const val defaultFocusSeconds = 10
    const val defaultRestSeconds = 5
}

// Explicit session state instead of bool soup
enum class PomodoroPhase { IDLE, FOCUS, REST, FINISHED }
enum class TimerStatus { RUNNING, PAUSED, STOPPED }

data class FocusUiState(
    val availableDurations: List<Int> = PomodoroDefaults.focusDurations,
    val availableSessions: List<Int> = PomodoroDefaults.sessions,
    val duration: Int = PomodoroDefaults.defaultFocusSeconds,
    val initialDuration: Int = PomodoroDefaults.defaultFocusSeconds,

    val currentSession: Int = 1,
    val totalSessions: Int = 1,
    val focusTimerStatus: TimerStatus = TimerStatus.STOPPED,
    val focusPhase: PomodoroPhase = PomodoroPhase.IDLE,
) {
    val progress: Float
        get() = if (initialDuration > 0) {
            1f - duration.toFloat() / initialDuration
        } else 0f
}

data class RestUiState(
    val availableDurations: List<Int> = PomodoroDefaults.restDurations,
    val duration: Int = PomodoroDefaults.defaultRestSeconds,
    val initialDuration: Int = PomodoroDefaults.defaultRestSeconds,
    val restTimerStatus: TimerStatus = TimerStatus.STOPPED,
    val restPhase: PomodoroPhase = PomodoroPhase.IDLE,
) {
    val progress: Float
        get() = if (initialDuration > 0) {
            1f - duration.toFloat() / initialDuration
        } else 0f
}
