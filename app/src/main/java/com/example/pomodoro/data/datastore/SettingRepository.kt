package com.example.pomodoro.data.datastore

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.pomodoro.data.datastore.zeroDayHoursDataPoints

import com.madrapps.plot.line.DataPoint
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.collections.*



interface SettingsRepository {
    suspend fun createHourlyFocusKey(): Preferences.Key<Int>
    suspend fun saveHourlyFocusDuration(duration: Int)

}

enum class ViewMode {
    YearMonth,
    YearWeek,
    YearDay,
    MonthDay,
    WeekDay,
    DayHour,
}


val zeroYearWeeksDataPoints: List<DataPoint> = List(52) { index ->
    DataPoint(index.toFloat(), 0f)
}
val zeroYearMonthsDataPoints: List<DataPoint> = List(12) { index ->
    DataPoint(index.toFloat(), 0f)
}
val zeroYearDaysDataPoints: List<DataPoint> = List(365) { index ->
    DataPoint(index.toFloat(), 0f)
}

val zeroMonthDaysDataPoints: List<List<DataPoint>> = buildList{
    repeat(12) {
        add(List(31) { DataPoint(it.toFloat(), 0f) })
    }
}
val zeroWeekDaysDataPoints: List<List<DataPoint>> = buildList{
    repeat(10) {
        add(List(7) { DataPoint(it.toFloat(), 0f) })
    }
}
val zeroDayHoursDataPoints: List<DataPoint> = buildList {
    repeat(24) {
        add(DataPoint(it.toFloat(), 0f))
    }
}
    @RequiresApi(Build.VERSION_CODES.O)
    data class ChartState  (
        val chartDataYearMonths: List<DataPoint> = zeroYearMonthsDataPoints,
        val chartDataYearWeeks: List<DataPoint> = zeroYearWeeksDataPoints,
        val chartDataYearDays: List<DataPoint> = zeroYearDaysDataPoints,
        val chartDataWeekDays: Map<String, List<DataPoint>>? = null,
        val chartDataMonthDays: Map<String, List<DataPoint>>? = null,
        val chartDataDayHours: Map<String, List<DataPoint>>? = null,
    )
data class ChartUpdate (
    val availableDays: List<String> = listOf("06 10 2025"),
    val dateHourDataPoint: List<DataPoint> = zeroDayHoursDataPoints
)


    @Singleton
    @RequiresApi(Build.VERSION_CODES.O)
    class SettingsRepositoryImpl @Inject constructor( //this tells Hilt that I need to inject this dependency in the constructor to build this class -> Hilt looks at it at compile time -> draw the graph -> then at run time -> it will inject the dependency
        private val dataStore: DataStore<Preferences>,
        private val scope: CoroutineScope
    ) : SettingsRepository {

        private val LAST_FOCUS_KEY: Preferences.Key<String> =
            stringPreferencesKey("last_active_time")
        private val formatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")
        private val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")


        private val _chartState = MutableStateFlow(ChartState())
        val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

        private val _chartUpdate = MutableStateFlow(ChartUpdate())
        val chartUpdate: StateFlow<ChartUpdate> = _chartUpdate.asStateFlow()


        override suspend fun createHourlyFocusKey(): Preferences.Key<Int> {
            val hourKey: String = LocalDateTime.now().format(formatter)
            Log.d("DEBUG", "createHourlyFocusKey: $hourKey")
            return intPreferencesKey(hourKey)
        }
        //a helper function to create a key for each hour.

        //everytime study countdown runs -> save 1 sec
        override suspend fun saveHourlyFocusDuration(duration: Int) {
            val hourKey: Preferences.Key<Int> = createHourlyFocusKey()
            val old: Int = dataStore.data.first()[hourKey] ?: 0
            Log.d("DEBUG", "saveHourlyFocusDuration: $old")
            dataStore.edit {
                it[hourKey] = old + duration  //save in "29 09 2025T0"
                it[hourKey]?.let { it1 -> //
                    if (it1 >= 10) { //only save as last focus if it is more than 10 secs
                        it[LAST_FOCUS_KEY] = LocalDate.now().format(formatterDay) // "26 09 2025"
                    }
                }
            }
        }

        fun getLastDayActive(): StateFlow<String?> {
            return dataStore.data.map {
                it[LAST_FOCUS_KEY]
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            ) //this only pull data, not affect anything
        }

        val hourList: List<Int> = List(24) { index -> index }

        var preferencesObj: Preferences? = null
        suspend fun getPreferencesObj(): Preferences  {
            preferencesObj = dataStore.data.first()
            return dataStore.data.first()
        }

        suspend fun generateChart(viewMode: ViewMode = ViewMode.DayHour) {
            val today: LocalDate = LocalDate.now()
            val preferencesObject: Preferences = getPreferencesObj()
            val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")

            when (viewMode) {
                ViewMode.YearDay -> TODO()
                ViewMode.YearMonth -> TODO()
                ViewMode.YearWeek -> TODO()
                ViewMode.MonthDay -> TODO()
                ViewMode.WeekDay -> TODO()
                ViewMode.DayHour -> {

                    val chartDataDayHours = preferencesObject.asMap()
                        .filterKeys {
                            regexDayHourKey.matches(it.name)
                            //return a Map that only contains keys that matches the form : "29 09 2025T0"
                        }.toList()
                        .groupBy { it.first.name.substringBefore("T") }
                        .mapValues { create24HoursKey(it.key) }

                    Log.d("DEBUG", "generateChart - DayData: $chartDataDayHours")
                    //generateChart - DayData: {06 10 2025=[(06 10 2025T16, 10)], 08 10 2025=[(08 10 2025T20, 20), (08 10 2025T21, 10)]}
                    updateChartState(chartDataDayHours = chartDataDayHours)
                    Log.d(
                        "DEBUG",
                        "generateChart - chartDataDayHours: ${chartState.value.chartDataDayHours}"
                    )
                    val availableDays = chartDataDayHours.keys.toList().reversed()
                    //the latest focus day is at index 0

                    _chartUpdate.update {
                        it.copy(
                            availableDays = availableDays,
                            dateHourDataPoint = chartDataDayHours.values.first()
                        )
                    }
                    pickDay(availableDays.first()) //pick the first day

                    Log.d("DEBUG", "generateChart - availableDays: $availableDays")
                }
            }
        }


        fun pickDay(date: String) {
            _chartUpdate.update {
                it.copy(
                    dateHourDataPoint = chartState.value.chartDataDayHours?.get(date) //get the lastest focus day
                        ?: zeroDayHoursDataPoints
                )
            }
        }

        private fun createHourKey(base: String, unit: Int): Preferences.Key<Int> {
            val padded = unit.toString().padStart(2, '0')
            return intPreferencesKey("${base}T$padded")
        }

        private  fun create24HoursKey(dateString: String): List<DataPoint> {
            val preferencesObject: Preferences? = preferencesObj
            val chartDataDay = hourList.mapIndexed { index, hour ->
                val key = createHourKey(dateString.format(formatterDay), hour)
                DataPoint(index.toFloat(), preferencesObject?.get(key)?.toFloat() ?: 0f)
            }
            return chartDataDay
        }


        // Helper functions for better organization
        private fun updateChartState(
            chartDataYearMonths: List<DataPoint>? = null,
            chartDataYearWeeks: List<DataPoint>? = null,
            chartDataYearDays: List<DataPoint>? = null,
            chartDataWeekDays: Map<String, List<DataPoint>>? = null,
            chartDataMonthDays: Map<String, List<DataPoint>>? = null,
            chartDataDayHours: Map<String, List<DataPoint>>? = null,

            ) {
            _chartState.update { old ->
                old.copy(
                    chartDataYearMonths = chartDataYearMonths ?: old.chartDataYearMonths,
                    chartDataYearWeeks = chartDataYearWeeks ?: old.chartDataYearWeeks,
                    chartDataYearDays = chartDataYearDays ?: old.chartDataYearDays,
                    chartDataWeekDays = chartDataWeekDays ?: old.chartDataWeekDays,
                    chartDataMonthDays = chartDataMonthDays ?: old.chartDataMonthDays,
                    chartDataDayHours = chartDataDayHours ?: old.chartDataDayHours,
                )
            }
        }
    }
