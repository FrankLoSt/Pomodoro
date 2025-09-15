package com.example.pomodoro.ui.components

import android.util.Log

// --- ViewModelCountDown.kt ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.data.PomodoroControllerImpl
import com.example.pomodoro.data.PomodoroState

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelCountDown : ViewModel() {

    private val controller = PomodoroControllerImpl(scope = viewModelScope)

    // Expose states for UI
    val pomodoroState: StateFlow<PomodoroState> = controller.pomodoroState
    val focusUiState: StateFlow<FocusUiState> = controller.focusUiState
    val restUiState: StateFlow<RestUiState> = controller.restUiState

    // --- Actions that map directly to controller ---
    fun configure(studyMinutes: Int, restMinutes: Int, sessions: Int) =
        controller.configure(studyMinutes, restMinutes, sessions)

    fun startCountDown() = controller.start()
    fun pause() = controller.pause()
    fun resume() = controller.resume()
    fun reset() = controller.reset()
    fun skipRest() = controller.skipRest()

    // --- Convenience helper for toggling pause/resume ---
    fun togglePauseResume() {
        if (pomodoroState.value is PomodoroState.Paused) {
            resume()
        } else {
            pause()
        }
    }

    // --- Utility ---
    fun formatter(duration: Int): String = controller.formatter(duration)
}
















