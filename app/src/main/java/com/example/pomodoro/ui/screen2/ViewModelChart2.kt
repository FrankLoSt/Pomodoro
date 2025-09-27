package com.example.pomodoro.ui.screen2

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.datastore.ChartState
import com.example.pomodoro.data.datastore.FocusChartRepository3

import com.example.pomodoro.data.datastore.SettingsRepository
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl
import com.example.pomodoro.data.datastore.SettingsRepositoryImpl2
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
class FocusChartViewModel @Inject constructor(
    private val repository: FocusChartRepository3
) : ViewModel() {

    // Expose the chart state directly from the repository
    val chartState: StateFlow<ChartState> = repository.chartState

    // Expose last active day
    val lastActiveDay: StateFlow<String?> = repository.getLastDayActive()

    /**
     * Entry point to trigger chart generation
     */
    fun loadChart(viewMode: ViewMode, weekStart: DayOfWeek = DayOfWeek.MONDAY) {
        viewModelScope.launch {
            repository.generateChart(viewMode, weekStart)
        }
    }
}
