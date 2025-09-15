package com.example.pomodoro.data

// --- UiState.kt ---
data class FocusUiState(
    // Study
    val listFocusDuration: List<Int> = (5..180 step 5).toList(),
    val listSessions: List<Int> = (1..30 step 1).toList(),
    val duration: Int = 10, //test with 10
    val initialDuration: Int = 10,//test with 10
    val isRunning: Boolean = true,
    val initialSessions: Int = 3,
    val sessions: Int = 3, //test with 3
    val isPause: Boolean = false,
) {
    // Helpers
    fun studyProgress(): Float =
        if (initialDuration > 0) 1f - duration.toFloat() / initialDuration else 0f
}

data class RestUiState(
    val listRestDuration: List<Int> = listOf(1) + (5..30 step 5).toList(),
    val restDuration: Int = 5, //test with 30
    val initialRestDuration: Int = 5,//test with 30
    val isStudying: Boolean = true,
    val isShowingMenu: Boolean =  true
) {
    fun restProgress(): Float =
        if (initialRestDuration > 0) 1f - restDuration.toFloat() / initialRestDuration else 0f
}