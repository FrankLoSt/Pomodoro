package com.example.pomodoro

// --- UiState.kt ---
data class FocusUiState(
    // Study
    val duration: Int = 25 * 60,
    val initialDuration: Int = 25 * 60,
    val isRunning: Boolean = false,
    //Rest

) {
    // Helpers
    fun studyProgress(): Float =
        if (initialDuration > 0) 1f - duration.toFloat() / initialDuration else 0f

}

data class RestUiState(
    val restDuration: Int = 5 * 60,
    val initialRestDuration: Int = 5 * 60,
    val isStudying: Boolean = false,
) {
    fun restProgress(): Float =
        if (initialRestDuration > 0) 1f - restDuration.toFloat() / initialRestDuration else 0f
}