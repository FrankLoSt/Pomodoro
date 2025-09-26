package com.example.pomodoro.ui.Screen1

// --- ViewModelCountDown.kt ---
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.data.PomodoroControllerImpl
import com.example.pomodoro.data.PomodoroPhase
import com.example.pomodoro.data.TimerStatus
import com.example.pomodoro.data.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class ViewModelCountDown @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {



    // Create controller tied to ViewModel scope
    private val controller = PomodoroControllerImpl(
        scope = viewModelScope,
        settingsRepository = settingsRepository
    )

    // Expose state flows
    val focusUiState: StateFlow<FocusUiState> = controller.focusUiState
    val restUiState: StateFlow<RestUiState> = controller.restUiState

    // --- Countdown control ---
    fun startCountDown() = controller.start()
    fun breakFun() = controller.breakFun()
    fun pause() = controller.pause()
    fun resume() = controller.resume()

    fun togglePauseResume() {
        Log.d("DEBUG", "togglePauseResume: focus: ${focusUiState.value.focusTimerStatus} and rest: ${restUiState.value.restTimerStatus}")
        Log.d("DEBUG", "togglePauseResume: focus: ${focusUiState.value.focusPhase} and rest: ${restUiState.value.restPhase}")
        if(
            (focusUiState.value.focusTimerStatus == TimerStatus.RUNNING && focusUiState.value.focusPhase == PomodoroPhase.FOCUS) || (restUiState.value.restTimerStatus == TimerStatus.RUNNING && restUiState.value.restPhase == PomodoroPhase.REST)) {
            pause()
        } else {
            resume()
        }
    }

    fun breakFunDialog() {
        if (
            (focusUiState.value.focusTimerStatus != TimerStatus.PAUSED && restUiState.value.restTimerStatus != TimerStatus.PAUSED)

            ) {
            //if isPause = true -> nothing happens, if false -> pause()
            togglePauseResume()
        }
    }

    // --- Configuration ---
    fun setDurationMinutes(minutes: Int) = controller.setDurationMinutes(minutes)
    fun setRestDurationMinutes(minutes: Int) = controller.setRestDurationMinutes(minutes)
    fun setSessions(sessions: Int) = controller.setSessions(sessions)

    // --- Formatter ---
    fun formatter(duration: Int): String = controller.formatter(duration)

    //Dialog controller
}
















