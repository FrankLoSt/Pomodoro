package com.example.pomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface PomodoroSettings {
    val uiState: StateFlow<UiState>

    fun setDuration(duration: Int)

    suspend fun countDownLogic()

    fun start ()
    fun giveUp()

    suspend fun restCountDown()

    fun formatter(duration: Int): String
}
data class UiState (
    val duration: Int = 25 * 60,
    val initialDuration: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isStudying: Boolean = true,
    val restDuration: Int = 5 * 60,
    val initialRestDuration: Int = 5 * 60
)

object PomodoroSettingsImpl : PomodoroSettings  {

    private val _uiState = MutableStateFlow(UiState())
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()



    override fun start(){
        _uiState.update {
            it.copy(isRunning = true)
        }
    }

    var remainingSeconds by mutableIntStateOf(25*60)

    override fun setDuration(duration: Int) {
        remainingSeconds = duration * 60
        _uiState.update{
            it.copy(
                duration = duration * 60,
                initialDuration = duration * 60
            )
        }
    }


    override suspend fun countDownLogic() {
        println("DEBUG: Countdown started with duration = ${uiState.value.duration}")
        _uiState.update { it.copy(isStudying = true) }
        while (true) {
            val current = _uiState.value
            if (!current.isRunning || current.duration <= 0) break
            println("DEBUG: new duration = ${current.duration} sec")
            delay(1000)
            _uiState.update { it.copy(duration = it.duration - 1) }
        }
        delay(1000)
        println("DEBUG: Countdown finished or stopped.")
        _uiState.update { it.copy(
            isRunning = false,
            duration = remainingSeconds,
            isStudying = false
        )
        }
    }

    override suspend fun restCountDown() {
            while(true) {
                println("DEBUG: Rest Countdown started with duration = ${uiState.value.restDuration}")
                val restDuration = uiState.value.restDuration
                delay(1000)
                if(!uiState.value.isRunning && !uiState.value.isStudying) {
                    _uiState.update { it.copy(
                        restDuration = restDuration - 1
                    )
                    }
                  }
            }
    }

    override fun giveUp() {
        _uiState.update {
            it.copy(
                isRunning = false,
                duration = remainingSeconds
            )
        }
    }

    override fun formatter(duration: Int): String {
        val m = duration / 60
        val s = duration % 60
        return String.format("%02d:%02d", m, s)
    }
}