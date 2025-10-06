package com.example.pomodoro.ui.screen2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.pomodoro.data.datastore.SettingsRepository
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
import com.example.pomodoro.data.datastore.ViewMode
import com.madrapps.plot.line.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.prefs.Preferences
import javax.inject.Inject




@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class ViewModelChart @Inject constructor (
    private val settingsRepository: SettingsRepositoryImpl,
): ViewModel() {
    init {
        trackweekYear()
        generateChart()
    }

    fun trackweekYear () = settingsRepository.trackweekYear()


    val lastDayActive: StateFlow<String?> = settingsRepository.getLastDayActive()
    val chartState = settingsRepository.chartState

    fun generateChart (viewMode: ViewMode = ViewMode.DayHour, weekStart: DayOfWeek = DayOfWeek.MONDAY) = viewModelScope.launch {
        settingsRepository.generateChart(viewMode, weekStart)
    }


}
