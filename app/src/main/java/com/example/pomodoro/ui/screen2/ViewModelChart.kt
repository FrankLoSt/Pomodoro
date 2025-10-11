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
        generateChart(ViewMode.DayHour)
    }

    val lastDayActive: StateFlow<String?> = settingsRepository.getLastDayActive()
    val chartState = settingsRepository.chartState
    val chartUpdate = settingsRepository.chartUpdate

    fun generateChart(
        viewMode: ViewMode = ViewMode.DayHour,
        weekStart: DayOfWeek = DayOfWeek.MONDAY
    ) = viewModelScope.launch { settingsRepository.generateChart(viewMode, weekStart) }


    fun pickDay(day: String) {
        settingsRepository.pickDay(day)
    }

    fun pickWeek(week: Int) {
        settingsRepository.pickWeek(week)
    }

    fun pickMonth(month: Int) {
        settingsRepository.pickMonth(month)
    }
    fun pickYear(year: Int) {
        settingsRepository.pickYear(year)
    }

}
