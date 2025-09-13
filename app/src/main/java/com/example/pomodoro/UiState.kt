package com.example.pomodoro

// --- UiState.kt ---
data class UiState(
    // Study
    val duration: Int = 25 * 60,
    val initialDuration: Int = 25 * 60,
    val isRunning: Boolean = false,

    //Rest
    val isStudying: Boolean = true,
    val restDuration: Int = (0.5 * 60).toInt(),
    val initialRestDuration: Int = (0.5 * 60).toInt()
) {
    // Helpers
    fun studyProgress(): Float =
        if (initialDuration > 0) 1f - duration.toFloat() / initialDuration else 0f

    fun restProgress(): Float =
        if (initialRestDuration > 0) 1f - restDuration.toFloat() / initialRestDuration else 0f
}
