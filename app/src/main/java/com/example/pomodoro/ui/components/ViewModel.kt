package com.example.pomodoro.ui.components

import android.util.Log

// --- ViewModelCountDown.kt ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.data.PomodoroControllerImpl

import kotlinx.coroutines.flow.StateFlow

class ViewModelCountDown(

) : ViewModel() {

    private val controller = PomodoroControllerImpl(scope = viewModelScope)
    // Expose controller's state directly (keeps single source of truth)
    val focusUiState: StateFlow<FocusUiState> = controller.focusUiState
    val restUiState: StateFlow<RestUiState> = controller.restUiState




    fun startCountDown() = controller.start() //call start() from controller


    fun giveUp() = controller.giveUp()


    fun toggleStartGiveUp() {
        if (focusUiState.value.isRunning) giveUp() else startCountDown()
    }



    fun setDurationMinutes(minutes: Int) = controller.setDurationMinutes(minutes)
    fun setRestDurationMinutes(minutes: Int) = controller.setRestDurationMinutes(minutes) // haven't allowed used to set rest duration
    fun setSessions(sessions: Int) = controller.setSessions(sessions)

    fun formatter(duration: Int): String = controller.formatter(duration)

    fun onSliderChangeTesting (progress: Float) {
        val min = 1
        val max = 10
        val minutes = ((min + (max - min) * progress).toInt() / 1) * 1 // round to nearest 5
        Log.d("ViewModelCountDown", "onSliderChangeTesting: $minutes")
        controller.setDurationMinutes(minutes) //call setDurationMinutes() from controller - with minutes  = 1 - 10 mins
    }
}














