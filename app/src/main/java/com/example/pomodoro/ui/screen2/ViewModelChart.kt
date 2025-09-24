package com.example.pomodoro.ui.screen2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.datastore.GapResult
import com.example.pomodoro.data.datastore.SettingsRepository
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
import com.madrapps.plot.line.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject





@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class ViewModelChart @Inject constructor (
    private val settingsRepository: SettingsRepositoryImpl
): ViewModel() {
    private val _gapResult = MutableStateFlow<GapResult>(GapResult.None)
    val gapResult: StateFlow<GapResult> = _gapResult.asStateFlow()

    val lastDayActive: StateFlow<String?> = settingsRepository.getLastDayActive()

    init {
        viewModelScope.launch {
            checkDataGap() //check dataGap first when the app is open
        }
    }

    fun checkDataGap() {
        viewModelScope.launch {
            val result = settingsRepository.fillMissingKeysWithRule()
            _gapResult.value = result //assign state
        }
    }
}
