package com.example.pomodoro.ui.Screen1

// --- ViewModelCountDown.kt ---
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.data.PomodoroControllerImpl
import com.example.pomodoro.data.TimerState
import com.example.pomodoro.data.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ViewModelCountDown @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    //----------------------STATE----------------------------
     val controller = PomodoroControllerImpl(scope = viewModelScope, settingsRepository = settingsRepository) //temporarily make it be able to access outside for testing
    // Expose controller's state directly (keeps single source of truth)
    val focusUiState: StateFlow<FocusUiState> = controller.focusUiState //reference to focusUiState in controller
    val restUiState: StateFlow<RestUiState> = controller.restUiState //reference to restUiState in controller



    //---------------BUTTON---------------------

    fun startCountDown() = controller.start() //call start() from controller
    fun breakFun() = controller.breakFun()
    //only turns on pause when users want to break if it is not already paused
    fun breakFunDialog() {
        if(focusUiState.value.timerState != TimerState.PAUSED) { //if isPause = true -> nothing happens, if false -> pause()
            togglePauseResume()
        } 
    }

    fun pause() = controller.pause()
    fun resume() = controller.resume()

    fun togglePauseResume () {
        if(focusUiState.value.timerState == TimerState.PAUSED) {
            resume()
        } else {
            pause()
        }
    }
    fun toggleisFinished() = controller.toggleFinished()


    //--------------SET UP -------------------------
    fun setDurationMinutes(minutes: Int) = controller.setDurationMinutes(minutes)
    fun setRestDurationMinutes(minutes: Int) = controller.setRestDurationMinutes(minutes)
    fun setSessions(sessions: Int) = controller.setSessions(sessions)




    fun formatter(duration: Int): String = controller.formatter(duration)

}














