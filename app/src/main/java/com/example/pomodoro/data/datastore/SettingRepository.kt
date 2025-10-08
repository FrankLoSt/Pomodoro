package com.example.pomodoro.data.datastore

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.ListItemDefaults
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
val zeroDayHoursDataPoints: List<List<DataPoint>> = buildList{
    repeat(10) {
        add(List(24) { DataPoint(it.toFloat(), 0f) })
    }
}

data class ChartState (
    val chartDataYearMonths: List<DataPoint> = zeroYearMonthsDataPoints,
    val chartDataYearWeeks: List<DataPoint> = zeroYearWeeksDataPoints,
    val chartDataYearDays: List<DataPoint> = zeroYearDaysDataPoints,
    val chartDataWeekDays: List<List<DataPoint>> = zeroWeekDaysDataPoints,
    val chartDataMonthDays: List<List<DataPoint>> = zeroMonthDaysDataPoints,
    val chartDataDayHours: List<List<DataPoint>> = zeroDayHoursDataPoints,
)

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class SettingsRepositoryImpl @Inject constructor( //this tells Hilt that I need to inject this dependency in the constructor to build this class -> Hilt looks at it at compile time -> draw the graph -> then at run time -> it will inject the dependency
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : SettingsRepository {

    private val LAST_FOCUS_KEY: Preferences.Key<String> = stringPreferencesKey("last_active_time")
    private val formatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")
    private val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")



    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

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

    fun getLastDayActive():StateFlow<String?> {
        return dataStore.data.map{
            it[LAST_FOCUS_KEY]
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        ) //this only pull data, not affect anything
    }

    val hourList: List<Int> = List(24) { index -> index}

    suspend fun generateChart (viewMode: ViewMode = ViewMode.DayHour) {
        val today: LocalDate = LocalDate.now()
        val preferencesObject: Preferences = dataStore.data.first()
        val regexDayHourKey : Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")

        when (viewMode) {
            ViewMode.YearDay -> TODO()
            ViewMode.YearMonth -> TODO()
            ViewMode.YearWeek -> TODO()
            ViewMode.MonthDay -> TODO()
            ViewMode.WeekDay -> TODO()
            ViewMode.DayHour -> {
                val hoursKey = hourList.mapIndexed{index, _  ->

                }

                val dayData = preferencesObject.asMap()
                    .filterKeys{
                   regexDayHourKey.matches(it.name)
                        //return a Map that only contains keys that matches the form : "29 09 2025T0"
                    }.toList()
                    .groupBy{it.first.name.substringBefore("T")}

                Log.d("DEBUG", "generateChart - DayData: $dayData")
                //generateChart - DayData: {06 10 2025=[(06 10 2025T16, 10)], 08 10 2025=[(08 10 2025T20, 20), (08 10 2025T21, 10)]}



            }
        }

    }


    // Helper functions for better organization
    private fun updateChartState(
        chartDataYearMonths: List<DataPoint>? = null,
        chartDataYearWeeks: List<DataPoint>? = null,
        chartDataYearDays: List<DataPoint>? = null,
        chartDataWeekDays: List<List<DataPoint>>? = null,
        chartDataMonthDays: List<List<DataPoint>>? = null,
        chartDataDayHours: List<List<DataPoint>>? = null,
    ) {
        _chartState.update { old ->
            old.copy(
                chartDataYearMonths = chartDataYearMonths ?: old.chartDataYearMonths,
                chartDataYearWeeks = chartDataYearWeeks ?: old.chartDataYearWeeks,
                chartDataYearDays = chartDataYearDays ?: old.chartDataYearDays,
                chartDataWeekDays = chartDataWeekDays ?: old.chartDataWeekDays,
                chartDataMonthDays = chartDataMonthDays ?: old.chartDataMonthDays,
                chartDataDayHours = chartDataDayHours ?: old.chartDataDayHours
            )
        }
    }


    suspend fun trackweekYear () {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek  // e.g., MONDAY, TUESDAY
        val dayName = dayOfWeek.name     // "MONDAY"
        val dayIndex = dayOfWeek.value   // 1 (Monday) to 7 (Sunday)
        val weekOfYear = today.get(WeekFields.ISO.weekOfYear())  // e.g., 39
        Log.d("DEBUG", "trackweekYear: $dayName, $dayIndex, $weekOfYear")
        Log.d("DEBUG", "Preference Object: ${dataStore.data.first()}")
    } //this is only for testing
}
