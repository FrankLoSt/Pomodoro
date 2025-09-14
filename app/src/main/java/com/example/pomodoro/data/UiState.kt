package com.example.pomodoro.data

// --- UiState.kt ---
data class FocusUiState(
    // Study\
    val listFocusDuration: List<Int> = (5..180 step 5).toList(),
    val listSessions: List<Int> = (1..30 step 1).toList(),
    val duration: Int = 10, //test with 10
    val initialDuration: Int = 10,//test with 10
    val isRunning: Boolean = false,
    //Rest

) {
    // Helpers
    fun studyProgress(): Float =
        if (initialDuration > 0) 1f - duration.toFloat() / initialDuration else 0f

}

data class RestUiState(
    val listRestDuration: List<Int> = listOf(1) + (5..30 step 5).toList(),
    val restDuration: Int = 30, //test with 30
    val initialRestDuration: Int = 30,//test with 30
    val isStudying: Boolean = false,
) {
    fun restProgress(): Float =
        if (initialRestDuration > 0) 1f - restDuration.toFloat() / initialRestDuration else 0f
}