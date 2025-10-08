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




    private fun generateDaysData(preferences: Preferences, year: String = "2025"): Map<LocalDate, Int> {
        preferences.asMap().keys.forEach { Log.d("DEBUG", "Raw key: '${it.name}'") }
        val daysData = preferences.asMap()
            .filterKeys { key ->
                val name: String = key.name
                val match: MatchResult? = Regex("""\d{2} \d{2} \d{4}T\d{2}""").matchEntire(name)

                if (match != null) {
                    val datePart = name.substringBefore("T")
                    Log.d("DEBUG", "generateDaysData - datePart: $datePart")
                    try {
                        val date = LocalDate.parse(datePart, formatterDay)
                        date.year == year.toInt()

                    } catch (e: Exception) {
                        false
                    }
                } else { false }
            }
            // Only keys like "26 09 2025T15"
            //filter out keys that do not match the pattern, return a new map that contains only key-value pairs that has keys match the given rule
            //mapNotNull{} only works with non-null values, automatically skip null values -> safe
            .mapNotNull { (key, value) ->
                val name: String = key.name //turn it from Preferences.Key<String> to String
                val datePart: String = name.substringBefore("T") //only get the date part, e.g. "26 09 2025"

                val date: LocalDate? = try {
                    Log.e("DEBUG", "generateDaysData - datePart: '$datePart'")
                    LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd MM yyyy"))
                } catch (e: Exception) {
                    Log.e("DEBUG", "Failed to parse datePart: '$datePart'", e)
                    null
                }
                Log.d("DEBUG", "generateDaysData - date: ${date}")
                //these lines are meant to transform keys from string to LocalDate object
                date?.let { it to (value as? Int ?: 0) }
                //this returns a List<Pair<LocalDate, Int>>
            }
            .groupBy({ it.first }, { it.second })
            //groupBy returns a Map<LocalDate, List<Int>>
            .mapValues { (_, values) -> values.sum() }
            //mapValues transform values from List<Int> to Int
        Log.d("DEBUG", "generateDaysData - daysData: is empty?  ${daysData.isEmpty()}")
        return daysData
    }

    //create a map of focus time for 365 or 366 days of the year
    //MONDAY by default
    suspend fun generateChart(
        viewMode: ViewMode = ViewMode.DayHour,
        weekStart: DayOfWeek = DayOfWeek.MONDAY,
        year: String = "2025"
    ) {
        val preferencesObj = dataStore.data.first()
        Log.d("DEBUG", "generateChart: what is in preferencesObj $preferencesObj")
        val selectedYear = year.toInt()
        val daysData = generateDaysData(preferencesObj, year)
        val threshold = 10
        val weekFields = WeekFields.of(weekStart, 1)

        when (viewMode) {
            ViewMode.YearMonth -> {
                val data = (1..12).map { month ->
                    val sameMonthData = daysData
                        .filter { it.key.monthValue == month }
                        .filter { it.value > threshold }
                    val totalFocusTime = sameMonthData.values.sum()
                    DataPoint(month.toFloat(), totalFocusTime.toFloat())
                }
                updateChartState(chartDataYearMonths = data)
            }

            ViewMode.YearWeek -> {
                val groupedWeeks = daysData
                    .filter { it.value > threshold }
                    .entries
                    .groupBy { it.key.get(weekFields.weekOfYear()) }

                val data = (1..52).map { week ->
                    val weekData = groupedWeeks[week]?.map { it.value } ?: emptyList()
                    val totalFocus = weekData.sum()
                    DataPoint(week.toFloat(), totalFocus.toFloat())
                }
                updateChartState(chartDataYearWeeks = data)
            }

            ViewMode.YearDay -> {
                val daysInYear = if (Year.of(selectedYear).isLeap) 366 else 365
                val data = (1..daysInYear).map { dayOfYear ->
                    val date = LocalDate.ofYearDay(selectedYear, dayOfYear)
                    val value = daysData[date]?.toFloat() ?: 0f
                    DataPoint(dayOfYear.toFloat(), value)
                }
                updateChartState(chartDataYearDays = data)
            }

            ViewMode.MonthDay -> {
                val data: List<List<DataPoint>> = (1..12).map { month ->
                    val daysInMonth: Int = YearMonth.of(selectedYear, month).lengthOfMonth()

                    val monthData: List<DataPoint> = (1..daysInMonth).map { day ->
                        val date: String = LocalDate.of(selectedYear, month, day).format(formatterDay)
                        val dateKey: LocalDate = LocalDate.parse(date, formatterDay)

                        //create an LocalDate object that has the given year, month, and day

                        val value: Float = daysData[dateKey]?.toFloat() ?: 0f

                        DataPoint(day.toFloat(), value)
                    }
                    monthData
                }
                updateChartState(chartDataMonthDays = data)
            }


            ViewMode.WeekDay -> {
                val groupedWeeks: Map<LocalDate, List<Map.Entry<LocalDate, Int>>> = daysData
                    .entries
                    .groupBy { it.key.with(weekFields.dayOfWeek(), 1) }



                val data: List< List <DataPoint> > = groupedWeeks.map { (startOfWeek, entries) ->
                    (0..6).map { offset ->
                        val date = startOfWeek.plusDays(offset.toLong())
                        val value = daysData[date]?.toFloat() ?: 0f
                        DataPoint(offset.toFloat(), value)
                    }
                }
                updateChartState(chartDataWeekDays = data)
            }

            ViewMode.DayHour -> {
                val groupedDays = daysData
                    .filter { it.value > threshold }
                    .keys
                    .sorted()

                val data = groupedDays.map { date ->
                    val dateKey = date.format(formatterDay)
                    val dayData = hourKeys.mapIndexed { index, hour ->
                        val hourKey = intPreferencesKey("${dateKey}T${hour.toString().padStart(2, '0')}")
                        DataPoint(index.toFloat(), preferencesObj[hourKey]?.toFloat() ?: 0f)
                    }
                    dayData
                }
                updateChartState(chartDataDayHours = data)
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


    fun trackweekYear () {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek  // e.g., MONDAY, TUESDAY
        val dayName = dayOfWeek.name     // "MONDAY"
        val dayIndex = dayOfWeek.value   // 1 (Monday) to 7 (Sunday)
        val weekOfYear = today.get(WeekFields.ISO.weekOfYear())  // e.g., 39
        Log.d("DEBUG", "trackweekYear: $dayName, $dayIndex, $weekOfYear")
    } //this is only for testing


}
