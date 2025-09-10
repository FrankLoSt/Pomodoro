package com.example.pomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModelCounDown: ViewModel() {
    var total by mutableIntStateOf(25 * 60)


    val countdown: String
        get() {
            val second = total % 60
            val minutes = total / 60
            return String.format("%02d:%02d", minutes, second)
        }

    var isRunning by mutableStateOf(false)

    fun start() {
        isRunning = true
        viewModelScope.launch {
            while (total > 0) {
                delay(1000)
                total -= 1
            }
        }
    }
}













