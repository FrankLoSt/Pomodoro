package com.example.pomodoro

import android.util.Log
import androidx.compose.runtime.collectAsState

// --- ViewModelCountDown.kt ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.FocusUiState
import com.example.pomodoro.PomodoroController
import com.example.pomodoro.PomodoroControllerImpl

import kotlinx.coroutines.flow.StateFlow

class ViewModelCountDown(

) : ViewModel() {

    private val controller = PomodoroControllerImpl(scope = viewModelScope)


    // Expose controller's state directly (keeps single source of truth)
    val uiState: StateFlow<FocusUiState> = controller.focusUiState

    val restUiState: StateFlow<RestUiState> = controller.restUiState

    fun startCountDown() = controller.start()
    fun giveUp() = controller.giveUp()
    fun toggleStartGiveUp() {
        if (uiState.value.isRunning) giveUp() else startCountDown()
    }

    fun setDurationMinutes(minutes: Int) = controller.setDurationMinutes(minutes)
    fun setRestDurationMinutes(minutes: Int) = controller.setRestDurationMinutes(minutes)

    fun formatter(duration: Int): String = controller.formatter(duration)

    fun onSliderChangeTesting (progress: Float) {
        val min = 1
        val max = 10
        val minutes = ((min + (max - min) * progress).toInt() / 1) * 1 // round to nearest 5
        Log.d("ViewModelCountDown", "onSliderChangeTesting: $minutes")
        controller.setDurationMinutes(minutes)
    }
}














