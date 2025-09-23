package com.example.pomodoro.ui.screen2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoro.data.datastore.SettingsRepository
import com.madrapps.plot.line.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject





@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class ViewModelChart @Inject constructor (
    private val settingsRepository: SettingsRepository
): ViewModel() {
    val scope = this.viewModelScope
    val mockStudyData: Map<Int, Int> = mapOf(
        0 to 0,
        1 to 0,
        2 to 0,
        3 to 0,
        4 to 0,
        5 to 0,
        6 to 1200,   // 20 mins
        7 to 1800,   // 30 mins
        8 to 0,
        9 to 2400,   // 40 mins
        10 to 3600,  // 1 hour
        11 to 0,
        12 to 900,   // 15 mins
        13 to 0,
        14 to 1500,  // 25 mins
        15 to 2700,  // 45 mins
        16 to 0,
        17 to 3000,  // 50 mins
        18 to 0,
        19 to 1800,  // 30 mins
        20 to 0,
        21 to 600,   // 10 mins
        22 to 0,
        23 to 0
    )
    //study time in second for each hour (14 -> 18h)


    fun getTodayFocusMinutesConverter() = settingsRepository.getTodayHourlyFocusMinutes()

    var chartData: List<DataPoint> = settingsRepository.todayHourlyFocus.entries.mapIndexed { index, entry ->
            DataPoint(x = index.toFloat(), y = entry.value.toFloat())
        }
}
