package com.example.pomodoro.ui.components

import android.util.Log

// --- ViewModelCountDown.kt ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.FocusUiState
import com.example.pomodoro.data.RestUiState
import com.example.pomodoro.data.PomodoroControllerImpl
import com.example.pomodoro.data.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.ManagedChannel

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelCountDown @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val channel: ManagedChannel
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        channel.shutdown() // or channel.shutdownNow()
    }

     val controller = PomodoroControllerImpl(scope = viewModelScope, settingsRepository = settingsRepository) //temporarily make it be able to access outside for testing
    // Expose controller's state directly (keeps single source of truth)
    val focusUiState: StateFlow<FocusUiState> = controller.focusUiState
    val restUiState: StateFlow<RestUiState> = controller.restUiState


    fun startCountDown() = controller.start() //call start() from controller


    fun breakFun() = controller.breakFun()

    fun pause() = controller.pause()
    fun resume() = controller.resume()

    fun togglePauseResume () {
        if(focusUiState.value.isPause) {
            resume()
        } else {
            pause()
        }
    }
    fun toggleisFinished() = controller.toggleisFinished()

    fun setDurationMinutes(minutes: Int) = controller.setDurationMinutes(minutes)
    fun setRestDurationMinutes(minutes: Int) = controller.setRestDurationMinutes(minutes)
    fun setSessions(sessions: Int) = controller.setSessions(sessions)

    fun formatter(duration: Int): String = controller.formatter(duration)

}














