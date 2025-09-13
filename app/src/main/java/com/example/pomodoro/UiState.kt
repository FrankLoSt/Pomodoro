package com.example.pomodoro

// --- UiState.kt ---
data class UiState(
    val duration: Int = 25 * 60,
    val initialDuration: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isStudying: Boolean = true,
    val restDuration: Int = 5 * 60,
    val initialRestDuration: Int = 5 * 60
) {
    // Helpers
    fun studyProgress(): Float =
        if (initialDuration > 0) 1f - duration.toFloat() / initialDuration else 0f

    fun restProgress(): Float =
        if (initialRestDuration > 0) 1f - restDuration.toFloat() / initialRestDuration else 0f
}
