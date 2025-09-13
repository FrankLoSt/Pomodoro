package com.example.pomodoro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.log

class ViewModelCountDown(
    private val pomodoroSettings: PomodoroSettings = PomodoroSettingsImpl
): ViewModel(){
    val uiState: StateFlow<UiState> = pomodoroSettings.uiState

    private var countDownJob: Job? = null


    fun startCountDown () {
        println("DEBUG: startCountDown() called")
        pomodoroSettings.start()
        countDownJob?.cancel()
        countDownJob = viewModelScope.launch {
            try {
                pomodoroSettings.countDownLogic()
                pomodoroSettings.restCountDown()
                Log.d("DEBUG", "DEBUG: countDownLogic finished")
            } catch (e: Exception) {
                println("DEBUG: Exception in countDownLogic -> ${e.message}")
            }
        }
    }

    fun giveUp () {
        pomodoroSettings.giveUp()
        countDownJob?.cancel()
    }

    fun toggleStartGiveUp () {
        if (uiState.value.isRunning) {
            giveUp()
        } else {
            startCountDown()
        }
    }

    fun formatter (duration: Int): String {
        return pomodoroSettings.formatter(duration)
    }

    fun onSliderChange(progress: Float) {
        val min = 5
        val max = 180
        val minutes = ((min + (max - min) * progress).toInt() / 5) * 5 // round to nearest 5
        pomodoroSettings.setDuration(minutes)
    }
    fun onSliderChangeTesting (progress: Float) {
        val min = 1
        val max = 10
        val minutes = ((min + (max - min) * progress).toInt() / 1) * 1 // round to nearest 5
        pomodoroSettings.setDuration(minutes)
    }
}













