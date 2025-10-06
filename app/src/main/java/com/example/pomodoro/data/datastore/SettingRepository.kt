package com.example.pomodoro.data.datastore

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.Year
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


val zeroDayHoursDataPoints: List<DataPoint> = List(24) { index ->
    DataPoint(index.toFloat(), 0f)
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

val zeroMonthDaysDataPoints: List<DataPoint> = List(31) { index ->
    DataPoint(index.toFloat(), 0f)
}
val zeroWeekDaysDataPoints: List<DataPoint> = List(7) { index ->
    DataPoint(index.toFloat(), 0f)
}


data class ChartState (
    val chartDataYearMonths: List<DataPoint> = zeroYearMonthsDataPoints,
    val chartDataYearWeeks: List<DataPoint> = zeroYearWeeksDataPoints,
    val chartDataYearDays: List<DataPoint> = zeroYearDaysDataPoints,
    val chartDataWeekDays: List<DataPoint> = zeroWeekDaysDataPoints,
    val chartDataMonthDays: List<DataPoint> = zeroMonthDaysDataPoints,
    val chartDataDayHours: List<List<DataPoint>> = zeroDayHoursDataPoints,
)

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class SettingsRepositoryImpl @Inject constructor( //this tells Hilt that I need to inject this dependency in the constructor to build this class -> Hilt looks at it at compile time -> draw the graph -> then at run time -> it will inject the dependency
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : SettingsRepository {

    private val LAST_FOCUS_KEY = stringPreferencesKey("last_active_time")
    private val formatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")
    private val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")

    val hourKeys = (0..23).toList()
    val weekKeys = (1..52).toList()
    val monthKeys = (1..12).toList()

    val viewModeList = listOf("YearMonth", "YearWeek", "YearDay", "MonthDay", "WeekDay", "DayHour")
    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    override suspend fun createHourlyFocusKey(): Preferences.Key<Int> {
        val hourKey = LocalDateTime.now().format(formatter)
        Log.d("DEBUG", "createHourlyFocusKey: $hourKey")
        return intPreferencesKey(hourKey)
    }

    //a helper function to create a key for each hour.

    override suspend fun saveHourlyFocusDuration(duration: Int) {

        val hourKey = createHourlyFocusKey()
        val old = dataStore.data.first()[hourKey] ?: 0
        Log.d("DEBUG", "saveHourlyFocusDuration: $old")
        dataStore.edit {
            it[hourKey] = old + duration  //save in "29 09 2025T0"
            it[hourKey]?.let { it1 -> //
                if (it1 >= 10) {
                    it[LAST_FOCUS_KEY] = LocalDateTime.now().format(formatter).toString() // "26 09 2025T21"
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




    private fun generateDaysData(preferences: Preferences): Map<LocalDate, Int> {
        val daysData = preferences.asMap()
            .filterKeys { it.name.matches(Regex("""\d{2} \d{2} \d{4}T\d{2}""")) } // Only keys like "26 09 2025T15"
            //filter out keys that do not match the pattern, return a new map that contains only key-value pairs that has keys match the given rule
            //mapNotNull{} only works with non-null values, automatically skip null values -> safe
            .mapNotNull { (key, value) ->
                val name = key.name //turn it from Preferences.Key<String> to String
                val datePart = name.substringBefore("T") //only get the date part, e.g. "26 09 2025"
                val date = try {
                    LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd MM yyyy"))
                } catch (e: Exception) {
                    null
                }
                //these lines are meant to transform keys from string to LocalDate object
                date?.let { it to (value as? Int ?: 0) }
                    //this returns a Pair<LocalDate, Int>  if date is not null, else null -> then skip
            }
            .groupBy({ it.first }, { it.second })
            //groupBy returns a Map<LocalDate, List<Int>>
            .mapValues { (_, values) -> values.sum() }
            //mapValues transform values from List<Int> to Int
        Log.d("DEBUG", "generateDaysData: $daysData")
        return daysData
    }

    //create a map of focus time for 365 or 366 days of the year
    //MONDAY by default
    suspend fun generateChart (viewMode: ViewMode = ViewMode.DayHour, weekStart: DayOfWeek = DayOfWeek.MONDAY) {
        val preferencesObj = dataStore.data.first()
        Log.d("DEBUG", "generateChart: $preferencesObj")
        val today = LocalDate.now()
        val daysData = generateDaysData(preferencesObj)

        val threshold = 10

        when (viewMode) {
            ViewMode.YearMonth -> {
                val data = monthKeys.mapIndexed{ index, month ->
                    val sameMonthData = daysData.filter{it.key.monthValue == month}//filter days in the same month
                        .filter{it.value > threshold} //only days that have focus time > 300s or 5 min is displayed, else = 0
                    val totalFocusTime = sameMonthData.values.sum()
                    DataPoint((index+1).toFloat(), totalFocusTime.toFloat())
                }
                updateChartState(chartDataYearMonths = data)
            }

            ViewMode.YearWeek -> {
                val weekFields = WeekFields.of(weekStart, 1)
                val data = weekKeys.mapIndexed { index, week ->
                    val sameWeekData =
                        daysData.filter { it.key.get(weekFields.weekOfYear()) == week }//filter days in the same week
                            .filter { it.value > threshold } //only days that have focus time > 300s or 5 min is displayed, else = 0
                    val totalFocusTime = sameWeekData.values.sum()
                    DataPoint((index+1).toFloat(), totalFocusTime.toFloat())
                }
                updateChartState(chartDataYearWeeks = data)
            }

            ViewMode.YearDay ->  {
                val daysInYear = if (today.isLeapYear) 366 else 365
                val data = (0 until daysInYear).mapIndexed { index, offset ->
                    val date = LocalDate.ofYearDay(today.year, offset + 1)
                    val value = daysData[date]?.toFloat() ?: 0f
                    DataPoint((index + 1).toFloat(), value)
                }
                updateChartState(chartDataYearDays = data)
            }
            ViewMode.MonthDay -> {
                val daysInMonth = today.lengthOfMonth()
                val data = (1..daysInMonth).mapIndexed { index, day ->
                    val date = LocalDate.of(today.year, today.month, day)
                    val value = daysData[date]?.toFloat() ?: 0f
                    DataPoint(index.toFloat(), value)
                }
                updateChartState(chartDataMonthDays = data)
            }
            ViewMode.WeekDay -> {
                val startOfWeek = today.with(WeekFields.of(weekStart, 1).dayOfWeek(), 1)
                val data = (0..6).mapIndexed { index, offset ->
                    val date = startOfWeek.plusDays(offset.toLong())
                    val value = daysData[date]?.toFloat() ?: 0f
                    DataPoint(index.toFloat(), value)
                }
                updateChartState(chartDataWeekDays = data)
            }

            ViewMode.DayHour -> {
                val todayKey = today.format(formatterDay) //string
                //todayKey: 06 10 2025
                val converter = DateTimeFormatter.ofPattern("dd MM yyyy")
                val todayDate = LocalDate.parse(todayKey, converter)
                val totalFocus = daysData[todayDate] ?: 0


                if (totalFocus < threshold) { // <10s → no data ONLY FOR TESTING
                    updateChartState(chartDataDayHours = emptyList())
                    Log.d("DEBUG", "No focus data recorded for $todayKey")
                    return
                }

                val data = hourKeys.mapIndexed { index, hour ->
                    val hourKey = intPreferencesKey("${todayKey}T${hour.toString().padStart(2, '0')}")
                    DataPoint(index.toFloat(), preferencesObj[hourKey]?.toFloat() ?: 0f)
                }
                updateChartState(chartDataDayHours = data)
            }
        }
    }



    // Helper functions for better organization


    private fun updateChartState(
        chartDataYearMonths: List<DataPoint> = emptyList(),
        chartDataYearWeeks: List<DataPoint> = emptyList(),
        chartDataYearDays: List<DataPoint> = emptyList(),
        chartDataWeekDays: List<List<DataPoint>> = emptyList(),
        chartDataMonthDays: List<DataPoint> = emptyList(),
        chartDataDayHours: List<List<DataPoint>> = emptyList(),
      //this will not work because line graph does not take empty list. It needs at least one data point
    ) {
        _chartState.update {
            it.copy(
                chartDataYearMonths = chartDataYearMonths,
                chartDataYearWeeks = chartDataYearWeeks,
                chartDataYearDays = chartDataYearDays,
                chartDataWeekDays = chartDataWeekDays,
                chartDataMonthDays = chartDataMonthDays,
                chartDataDayHours = chartDataDayHours,
            )
        }
        Log.d("DEBUG", "Chart updated: Day=${chartDataDayHours.size}, Week=${chartDataWeekDays.size}, Month=${chartDataMonthDays.size}")
    }

    fun trackweekYear () {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek  // e.g., MONDAY, TUESDAY
        val dayName = dayOfWeek.name     // "MONDAY"
        val dayIndex = dayOfWeek.value   // 1 (Monday) to 7 (Sunday)
        val weekOfYear = today.get(WeekFields.ISO.weekOfYear())  // e.g., 39
        Log.d("DEBUG", "trackweekYear: $dayName, $dayIndex, $weekOfYear")
    } //this is only for testing


}
