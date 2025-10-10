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
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import co.yml.charts.common.model.Point
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
import kotlin.collections.map


interface SettingsRepository {
    suspend fun createHourlyFocusKey(): Preferences.Key<Int>
    suspend fun saveHourlyFocusDuration(duration: Int)

    suspend fun generateChart(viewMode: ViewMode,weekStart: DayOfWeek) {}

}

enum class ViewMode {
    YearMonth,
    YearWeek,
    YearDay,
    MonthDay,
    WeekDay,
    DayHour,
}


val zeroYearWeeksDataPoints: List<Point> = List(52) { index ->
    Point(index.toFloat(), 0f)
}
val zeroYearMonthsDataPoints: List<Point> = List(12) { index ->
    Point(index.toFloat(), 0f)
}
val zeroYearDaysDataPoints: List<Point> = List(365) { index ->
    Point(index.toFloat(), 0f)
}

val zeroMonthDaysDataPoints: List<List<Point>> = buildList{
    repeat(12) {
        add(List(31) { Point(it.toFloat(), 0f) })
    }
}
val zeroWeekDaysDataPoints: List<Point> = buildList{
    repeat(7) {
        add(Point(it.toFloat(), 0f))
    }
}

val zeroDayHoursDataPoints: List<Point> = buildList {
    repeat(24) {
        add(Point(it.toFloat(), 0f))
    }
}
    @RequiresApi(Build.VERSION_CODES.O)
    data class ChartState  (
        val chartDataYearMonths: List<Point> = zeroYearMonthsDataPoints,
        val chartDataYearWeeks: List<Point> = zeroYearWeeksDataPoints,
        val chartDataYearDays: List<Point> = zeroYearDaysDataPoints,
        val chartDataWeekDays: Map<String, List<Point>>? = null,
        val chartDataMonthDays: Map<String, List<Point>>? = null,
        val chartDataDayHours: Map<String, List<Point>>? = null,
    )


data class ChartUpdate (
    val viewMode: ViewMode = ViewMode.DayHour,
    val availableDays: List<String> = listOf("No Data"),
    val dateHourDataPoint: List<Point> = zeroDayHoursDataPoints,
    val availableWeeks: List<String> = listOf("No Data"),
    val weekDayDataPoints: List<Point> = zeroWeekDaysDataPoints
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

    val todayTimeKey: LocalDateTime = LocalDateTime.now()
    val todayKey: LocalDate = todayTimeKey.toLocalDate()

    override suspend fun createHourlyFocusKey(): Preferences.Key<Int> {
        val hourKey: String = todayTimeKey.format(formatter)
        Log.d("DEBUG", "createHourlyFocusKey: $hourKey")
        return intPreferencesKey(hourKey)
    }
    //a helper function to create a key for each hour.

    //everytime study countdown runs -> save 1 sec
    override suspend fun saveHourlyFocusDuration(duration: Int) {
        val hourKey: Preferences.Key<Int> = createHourlyFocusKey()
        dataStore.edit {
            val old: Int = it[hourKey] ?: 0
            Log.d("DEBUG", "saveHourlyFocusDuration: $old")
            val newVal = old + duration

            it[hourKey] = newVal //save in "29 09 2025T0"
            it[hourKey]?.let { it1 -> //
                if (it1 >= 10) { //only save as last focus if it is more than 10 secs - TESTING
                    it[LAST_FOCUS_KEY] = LocalDate.now().format(formatterDay) // "26 09 2025"
                }
            }
        }
    }

    fun getLastDayActive(): StateFlow<String?> {

        //dataStore.data returns a Flow<Preferences>
        //dataStore.data.first() returns a Preferences object, which is always the latest value of the data store

        return dataStore.data.map {
            it[LAST_FOCUS_KEY]
            //this transform the whole fucking Preferences object into a String?, also fetch data of last focus key
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        ) //this only pull data, not affect anything
    }

    val hourList: List<Int> = List(24) { index -> index }


    suspend fun getPreferencesObj(): Preferences {
        return dataStore.data.first()
    }


    override suspend fun generateChart(viewMode: ViewMode, weekStart: DayOfWeek) {

        _chartUpdate.update { it.copy(viewMode = viewMode) }

        val todayKey: LocalDate = todayKey
        val preferencesObject: Preferences = getPreferencesObj()
        val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")
        val weekFields: WeekFields = WeekFields.of(weekStart, 1)

        when (viewMode) {
            ViewMode.YearDay -> TODO()
            ViewMode.YearMonth -> TODO()
            ViewMode.YearWeek -> TODO()
            ViewMode.MonthDay -> TODO()
            ViewMode.WeekDay -> {
                val totalFocusOfADay = preferencesObject.asMap()
                    .filterKeys {
                        regexDayHourKey.matches(it.name)
                        //return a Map that only contains keys that matches the form : "29 09 2025T0"
                    }.toList()
                    .groupBy { it.first.name.substringBefore("T") }
                    //this will just return an empty Map if preferencesObject is empty
                    .mapValues { values ->
                        values.value.sumOf{
                            pair ->
                            pair.second.toString().toIntOrNull() ?:0}
                    }
                //{26 09 2025=401, 27 09 2025=222, 04 10 2025=86, 05 10 2025=30, 06 10 2025=10}

                val listDays: List<LocalDate> = totalFocusOfADay.map{LocalDate.parse(it.key, formatterDay)}

                val firstDayOfYear = LocalDate.of(todayKey.year, 1, 1)

                val listWeeks: Map<String, List<Point>> = buildMap {
                    listDays.map { it.get(weekFields.weekOfYear()) }
                        .toSet()
                        .forEach { weekNumber ->
                            val firstWeekDate = firstDayOfYear.with(weekFields.weekOfYear(), weekNumber.toLong())

                            val startOfWeek = firstWeekDate.with(weekFields.dayOfWeek(), 1) // Monday

                            val datesInWeek: List<String> = (0..6).map { startOfWeek.plusDays(it.toLong()).format(formatterDay) }

                            Log.e("DEBUG", "generateChart - datesInWeek: $datesInWeek")

                            put(weekNumber.toString(), datesInWeek)
                        }//this returns Map<String, List<String>>
                }.mapValues { entry ->
                    entry.value.mapIndexed { index, date ->
                        Log.e("DEBUG", "generateChart - index: $index")
                        Point(index.toFloat(), totalFocusOfADay[date]?.toFloat() ?: 0f)
                    }
                }//transform string -> DataPoint

                updateChartState(chartDataWeekDays = listWeeks)
                Log.d("DEBUG", "generateChart - chartDataWeekDays: ${chartState.value.chartDataWeekDays}")
                val availableWeek = listWeeks.keys.toList().sortedDescending()
                Log.d("DEBUG", "generateChart - availableWeek: $availableWeek")

                if(availableWeek.isNotEmpty()) {
                    _chartUpdate.update {
                        it.copy(
                            availableWeeks = availableWeek,
                            weekDayDataPoints = listWeeks.values.first()
                        )
                    }
                    Log.e("DEBUG", "generateChart - weekDayDataPoints: ${chartUpdate.value.weekDayDataPoints}")
                    pickWeek(availableWeek.first())
                } else { _chartUpdate.update {
                        it.copy(
                            availableWeeks = listOf("No data"),
                            weekDayDataPoints = zeroWeekDaysDataPoints
                        )
                    } }
            }

            ViewMode.DayHour -> {

                val chartDataDayHours: Map<String, List<Point>> = preferencesObject.asMap()
                    .filterKeys {
                        regexDayHourKey.matches(it.name)
                        //return a Map that only contains keys that matches the form : "29 09 2025T0"
                    }.toList()
                    .groupBy { it.first.name.substringBefore("T") }
                    //this will just return an empty Map if preferencesObject is empty
                    .mapValues { create24HoursKey(it.key, preferencesObject) }


                //generateChart - DayData: {06 10 2025=[(06 10 2025T16, 10)], 08 10 2025=[(08 10 2025T20, 20), (08 10 2025T21, 10)]}

                updateChartState(chartDataDayHours = chartDataDayHours)

                Log.d(
                    "DEBUG",
                    "generateChart - chartDataDayHours: ${chartState.value.chartDataDayHours}"
                )

                val availableDays = chartDataDayHours.keys.toList().sortedDescending()
                //the latest focus day is at index 0
                if (availableDays.isNotEmpty()) {
                    _chartUpdate.update {
                        it.copy(
                            availableDays = availableDays,
                            dateHourDataPoint = chartDataDayHours.values.first()
                        )
                    }
                    pickDay(availableDays.first()) //pick the first day

                    Log.d("DEBUG", "generateChart - availableDays: $availableDays")
                } else {
                    _chartUpdate.update {
                        it.copy(
                            availableDays = listOf("No data"),
                            dateHourDataPoint = zeroDayHoursDataPoints
                        )
                    }
                }
            }
        }
    }


    fun pickDay(date: String) {
        _chartUpdate.update {
            it.copy(
                dateHourDataPoint = chartState.value.chartDataDayHours?.getOrDefault(
                    date,
                    zeroDayHoursDataPoints
                ) ?: zeroDayHoursDataPoints,
            )
            /*
                * if chartDataDayHours is not null -> getOrDefault, if the date does not exist -> return zeroDayHoursDataPoints,
                * if chartDataDayHours is null -> return zeroDayHoursDataPoints
                * => Always return a non-null, list of DataPoint.
                * */
        }
    }
    fun pickWeek(week: String = "No Data") {
        _chartUpdate.update {
            it.copy(
                weekDayDataPoints = chartState.value.chartDataWeekDays?.getOrDefault(
                    week,
                    zeroWeekDaysDataPoints
                ) ?: zeroWeekDaysDataPoints,
            )
        }
    }

    private fun createHourKey(base: String, unit: Int): Preferences.Key<Int> {
        val padded = unit.toString().padStart(2, '0')
        return intPreferencesKey("${base}T$padded")
    }

    private fun create24HoursKey(
        dateString: String,
        preferencesObject: Preferences? = null
    ): List<Point> {
        val chartDataDay = hourList.mapIndexed { index, hour ->
            val key = createHourKey(dateString.format(formatterDay), hour)
            Point(index.toFloat(), preferencesObject?.get(key)?.toFloat() ?: 0f)
        }
        return chartDataDay
    }

    //private fun createAWeekKey(week: String, preferencesObject: Preferences): List<DataPoint> {}


    // Helper functions for better organization
    private fun updateChartState(
        chartDataYearMonths: List<Point>? = null,
        chartDataYearWeeks: List<Point>? = null,
        chartDataYearDays: List<Point>? = null,
        chartDataWeekDays: Map<String, List<Point>>? = null,
        chartDataMonthDays: Map<String, List<Point>>? = null,
        chartDataDayHours: Map<String, List<Point>>? = null,
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
