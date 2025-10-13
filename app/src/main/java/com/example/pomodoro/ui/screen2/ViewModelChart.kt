package com.example.pomodoro.ui.screen2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
import com.example.pomodoro.data.datastore.ViewMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject


@HiltViewModel
class ViewModelChart @Inject constructor (
    private val settingsRepository: SettingsRepositoryImpl,
): ViewModel() {
    init {
        generateChart(ViewMode.Day)
    }

    val lastDayActive: StateFlow<String?> = settingsRepository.getLastDayActive()
    val chartState = settingsRepository.chartState
    val chartUpdate = settingsRepository.chartUpdate

    fun generateChart(
        viewMode: ViewMode = ViewMode.Day,
        weekStart: DayOfWeek = DayOfWeek.MONDAY
    ) = viewModelScope.launch { settingsRepository.generateChart(viewMode, weekStart) }


    fun pickDay(viewMode: ViewMode, leftOrRight: Boolean) {
        settingsRepository.pickDay(viewMode, leftOrRight)
    }


}
