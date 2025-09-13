package com.example.pomodoro

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.log

// --- ViewModelCountDown.kt ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow

class ViewModelCountDown(
    private val controller: PomodoroController
) : ViewModel() {

    // Expose controller's state directly (keeps single source of truth)
    val uiState: StateFlow<UiState> = controller.uiState

    fun startCountDown() = controller.start()
    fun giveUp() = controller.giveUp()
    fun toggleStartGiveUp() {
        if (uiState.value.isRunning) giveUp() else startCountDown()
    }

    fun setDurationMinutes(minutes: Int) = controller.setDurationMinutes(minutes)
    fun setRestDurationMinutes(minutes: Int) = controller.setRestDurationMinutes(minutes)

    fun formatter(duration: Int): String = controller.formatter(duration)
}














