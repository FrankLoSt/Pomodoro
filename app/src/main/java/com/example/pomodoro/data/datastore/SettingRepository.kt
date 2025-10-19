package com.example.pomodoro.data.datastore


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import co.yml.charts.common.model.Point
import com.example.pomodoro.data.MonsterFightingHourlyFocus
import com.example.pomodoro.ui.pickmonster.MonsterDataControllerImpl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import javax.inject.Inject
import javax.inject.Singleton


interface SettingsRepository {
    suspend fun createHourlyFocusKey(): Preferences.Key<Int>
    suspend fun saveHourlyFocusDuration(duration: Int)

    suspend fun generateChart(viewMode: ViewMode = ViewMode.Day,weekStart: DayOfWeek = DayOfWeek.MONDAY, year: Int = 2025) {}

}

enum class ViewMode {
    Year,
    Month,
    Week,
    Day,
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

val zeroMonthDaysDataPoints: List<Point> = buildList{
    repeat(30) {
        add( Point(it.toFloat(), 0f))
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

   //this stores all data for the chart
    data class ChartState  (
        val chartDataYearWeeks: List<Point> = zeroYearWeeksDataPoints,
        val chartDataYearDays: List<Point> = zeroYearDaysDataPoints,


        val chartDataYearMonths: Map<Int, List<Point>>?  = null,
        val chartDataWeekDays: Map<Int, List<Point>>? = null,
        val chartDataMonthDays: Map<Int, List<Point>>? = null,
        val chartDataDayHours: Map<String, List<Point>>? = null,
    )

//This is what is displayed in the UI chart
data class ChartUpdate (
    val viewMode: ViewMode = ViewMode.Day,

    val availableDays: List<String> = listOf("No Data"),
    val dateHourDataPoint: List<Point> = zeroDayHoursDataPoints,
    val dayIndex: Int = 0,

    val availableWeeks: List<Int> = listOf(0),
    val weekDayDataPoints: List<Point> = zeroWeekDaysDataPoints,
    val weekIndex: Int = 0,
    val startAndEndWeek: List<Pair<LocalDate, LocalDate>> = emptyList(),

    val availableMonths : List<Int> = listOf(0),
    val monthDayDataPoints: List<Point> = zeroMonthDaysDataPoints,
    val monthIndex: Int = 0,

    val availableYears: List<Int> = listOf(0),
    val yearMonthDataPoints: List<Point> = zeroYearMonthsDataPoints,
    val yearIndex: Int = 0,
)


@Singleton
class SettingsRepositoryImpl @Inject constructor( //this tells Hilt that I need to inject this dependency in the constructor to build this class -> Hilt looks at it at compile time -> draw the graph -> then at run time -> it will inject the dependency
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
    private val monsterDataControllerImpl: MonsterDataControllerImpl
) : SettingsRepository {

    private val LAST_FOCUS_KEY: Preferences.Key<String> =
        stringPreferencesKey("last_active_time")

    private val formatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")

    private val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")

    private val formaterYearFirst = DateTimeFormatter.ofPattern("yyyy MM dd")


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

    val preferencesObj: Preferences = preferencesOf(
        intPreferencesKey("26 09 2025T15") to 10,
        stringPreferencesKey("last_active_time") to "06 10 2025T08",
        intPreferencesKey("26 09 2025T16") to 30,
        intPreferencesKey("26 09 2025T18") to 118,
        intPreferencesKey("26 09 2025T19") to 52,
        intPreferencesKey("26 09 2025T20") to 129,
        intPreferencesKey("26 09 2025T21") to 62,
        intPreferencesKey("27 09 2025T00") to 110,
        intPreferencesKey("27 09 2025T01") to 65,
        intPreferencesKey("27 09 2025T02") to 10,
        intPreferencesKey("27 09 2025T03") to 16,
        intPreferencesKey("27 09 2025T13") to 11,
        intPreferencesKey("27 09 2025T14") to 10,
        intPreferencesKey("04 10 2025T08") to 4,
        intPreferencesKey("04 10 2025T15") to 28,
        intPreferencesKey("04 10 2025T16") to 34,
        intPreferencesKey("04 10 2025T20") to 20,
        intPreferencesKey("05 10 2025T20") to 30,
        intPreferencesKey("06 10 2025T08") to 10,

        // October continued
        intPreferencesKey("07 10 2025T09") to 45,
        intPreferencesKey("08 10 2025T10") to 60,
        intPreferencesKey("09 10 2025T11") to 75,
        intPreferencesKey("10 10 2025T12") to 90,
        intPreferencesKey("11 10 2025T13") to 105,
        intPreferencesKey("12 10 2025T14") to 120,
        intPreferencesKey("13 10 2025T15") to 135,
        intPreferencesKey("14 10 2025T16") to 150,

        // November
        intPreferencesKey("01 11 2025T08") to 20,
        intPreferencesKey("02 11 2025T09") to 25,
        intPreferencesKey("03 11 2025T10") to 30,
        intPreferencesKey("04 11 2025T11") to 35,
        intPreferencesKey("05 11 2025T12") to 40,
        intPreferencesKey("06 11 2025T13") to 45,
        intPreferencesKey("07 11 2025T14") to 50,
        intPreferencesKey("08 11 2025T15") to 55,

        // December
        intPreferencesKey("01 12 2025T08") to 60,
        intPreferencesKey("02 12 2025T09") to 65,
        intPreferencesKey("03 12 2025T10") to 70,
        intPreferencesKey("04 12 2025T11") to 75,
        intPreferencesKey("05 12 2025T12") to 80,
        intPreferencesKey("06 12 2025T13") to 85,
        intPreferencesKey("07 12 2025T14") to 90,
        intPreferencesKey("08 12 2025T15") to 95,

        // January
        intPreferencesKey("10 01 2025T08") to 100,
        intPreferencesKey("11 01 2025T09") to 105,
        intPreferencesKey("12 01 2025T10") to 110,
        intPreferencesKey("13 01 2025T11") to 115,
        intPreferencesKey("14 01 2025T12") to 120,
        intPreferencesKey("15 01 2025T13") to 125,
        intPreferencesKey("16 01 2025T14") to 130,
        intPreferencesKey("17 01 2025T15") to 135,
        intPreferencesKey("18 01 2025T08") to 140,
        intPreferencesKey("19 01 2025T09") to 145,
        intPreferencesKey("20 01 2025T10") to 150,
        intPreferencesKey("21 01 2025T11") to 155,
        intPreferencesKey("22 01 2025T12") to 160,
        intPreferencesKey("23 01 2025T13") to 165,
        intPreferencesKey("24 01 2025T14") to 170,
        intPreferencesKey("25 01 2025T15") to 175,

        intPreferencesKey("01 02 2025T08") to 180,
        intPreferencesKey("02 02 2025T09") to 185,
        intPreferencesKey("03 02 2025T10") to 190,
        intPreferencesKey("04 02 2025T11") to 195,
        intPreferencesKey("05 02 2025T12") to 200,
        intPreferencesKey("06 02 2025T13") to 205,
        intPreferencesKey("07 02 2025T14") to 210,
        intPreferencesKey("08 02 2025T15") to 215,

        intPreferencesKey("15 03 2025T08") to 220,
        intPreferencesKey("16 03 2025T09") to 225,
        intPreferencesKey("17 03 2025T10") to 230,
        intPreferencesKey("18 03 2025T11") to 235,
        intPreferencesKey("19 03 2025T12") to 240,
        intPreferencesKey("20 03 2025T13") to 245,
        intPreferencesKey("21 03 2025T14") to 250,
        intPreferencesKey("22 03 2025T15") to 240,

        intPreferencesKey("01 04 2025T08") to 20,
        intPreferencesKey("02 04 2025T09") to 25,
        intPreferencesKey("03 04 2025T10") to 30,
        intPreferencesKey("04 04 2025T11") to 35,
        intPreferencesKey("05 04 2025T12") to 40,
        intPreferencesKey("06 04 2025T13") to 45,
        intPreferencesKey("07 04 2025T14") to 50,
        intPreferencesKey("08 04 2025T15") to 55,


        intPreferencesKey("10 05 2025T08") to 60,
        intPreferencesKey("11 05 2025T09") to 65,
        intPreferencesKey("12 05 2025T10") to 70,
        intPreferencesKey("13 05 2025T11") to 75,
        intPreferencesKey("14 05 2025T12") to 80,
        intPreferencesKey("15 05 2025T13") to 85,
        intPreferencesKey("16 05 2025T14") to 90,
        intPreferencesKey("17 05 2025T15") to 95,

        intPreferencesKey("20 06 2025T08") to 100,
        intPreferencesKey("21 06 2025T09") to 105,
        intPreferencesKey("22 06 2025T10") to 110,
        intPreferencesKey("23 06 2025T11") to 115,
        intPreferencesKey("24 06 2025T12") to 120,
        intPreferencesKey("25 06 2025T13") to 125,
        intPreferencesKey("26 06 2025T14") to 130,
        intPreferencesKey("27 06 2025T15") to 135,

        )

    fun isLeapYear(year: Int): Boolean {
        return Year.of(year).isLeap
    }



    override suspend fun generateChart(viewMode: ViewMode, weekStart: DayOfWeek, year: Int) {
        _chartUpdate.update { it.copy(viewMode = viewMode) }

        val todayKey: LocalDate = todayKey
        val weekFields: WeekFields = WeekFields.of(weekStart, 1)
        val weekOfYearField: TemporalField = weekFields.weekOfYear()
        val dayOfWeekField: TemporalField = weekFields.dayOfWeek()

        val firstDayOfYear: LocalDate = LocalDate.of(todayKey.year, 1, 1)

       // val monsterHourFocusData = monsterDataControllerImpl.getHourFocusData()



        val totalFocusOfADay = monsterDataControllerImpl.getHourFocusData().groupBy {
            obj ->
            obj.hour.substringBefore("T")
        }.mapKeys{ entry ->
            val transformed = LocalDate.parse(entry.key, formaterYearFirst)
            transformed.format(formatterDay)
        }
            .mapValues { entry ->
            entry.value.sumOf{ monsterFightingHourlyFocus ->
                monsterFightingHourlyFocus.focusTime
            }
        }
        Log.d("ROOM", "generateChart - totalFocusOfADay: $totalFocusOfADay")

        val listDays: List<LocalDate> = totalFocusOfADay
            .mapNotNull { runCatching { LocalDate.parse(it.key, formatterDay) }.getOrNull() }

        val startAndEndWeek: MutableList<Pair<LocalDate, LocalDate>> = mutableListOf()

        when (viewMode) {

            ViewMode.Year -> {
                val dayFocusDataByACertainYear: Map<Int, Int> = totalFocusOfADay
                    .filterKeys { it.contains(year.toString()) }
                    //only date in the same year
                    .keys
                    .groupBy { day ->
                        runCatching {
                            LocalDate.parse(
                                day,
                                formatterDay
                            )
                        }.getOrNull()?.month ?: todayKey.month
                    }
                    .mapKeys { entry -> entry.key.value }
                    .mapValues { entry ->
                        entry.value.sumOf { day ->
                            totalFocusOfADay.getOrDefault(day, 0)
                        }
                    }
                val yearMonthDataChart: Map<Int, List<Point>> = buildMap {
                    val listMonths = (1..12).toList().mapIndexed { index, month ->
                        val monthFocus = dayFocusDataByACertainYear.getOrDefault(month, 0)
                        Point((index + 1).toFloat(), monthFocus.toFloat())
                    }
                    put(year, listMonths)
                }

                val availableYears = yearMonthDataChart.keys.toList().sortedDescending()

                // Log.d("DEBUG", "generateChart - availableYears: $availableYears")

                updateChartState(chartDataYearMonths = yearMonthDataChart)

                // Log.d("DEBUG", "generateChart - chartDataYearMonths: ${chartState.value.chartDataYearMonths}")

                _chartUpdate.update {
                    it.copy(
                        availableYears = availableYears.ifEmpty { listOf(0) },
                        yearMonthDataPoints = yearMonthDataChart[availableYears.firstOrNull()]
                            ?: zeroYearMonthsDataPoints
                    )
                }

            }

            ViewMode.Month -> {

                //totalFocusOfADay = {26 09 2025=401, 27 09 2025=222, 04 10 2025=86, 05 10 2025=30, 06 10 2025=10}


                val listAvailableMonths: List<Month> = totalFocusOfADay
                    .mapNotNull {
                        runCatching {
                            val parsedDate = LocalDate.parse(it.key, formatterDay)
                            Month.of(parsedDate.monthValue)
                        }.getOrNull()
                    }
                    .distinct()

                // listAvailableMonths = [SEPTEMBER, OCTOBER]

                val listMonthDaysPoints: Map<Int, List<Point>> = buildMap {
                    listAvailableMonths.map { month: Month ->

                        val dayNum = month.length(isLeapYear(todayKey.year))//this returns Int

                        val listDays: List<String> = buildList {
                            repeat(dayNum) {
                                val dateStr = runCatching {
                                    LocalDate.of(todayKey.year, month, it + 1).format(formatterDay)
                                }.getOrNull()

                                if (dateStr != null) add(dateStr)
                            }
                        }

                        val monthIntValue = month.value

                        put(monthIntValue, listDays)

                    }
                }.mapValues { entry ->
                    entry.value.mapIndexed { index, date ->
                        Point(index.toFloat(), totalFocusOfADay[date]?.toFloat() ?: 0f)
                    }
                }
                // listMonthDaysPoints =
                // {September=[Point(x=0.0, y=401.0), Point(x=1.0, y=222.0), Point(x=2.0, y = 86.0), ...],
                // October=[Point(x=0.0, y=30.0), Point(x=1.0, y=10.0)]....}

                updateChartState(chartDataMonthDays = listMonthDaysPoints)

                //  Log.d("DEBUG", "generateChart - chartDataMonthDays: ${chartState.value.chartDataMonthDays}")

                val availableMonths = listMonthDaysPoints.keys.toList().sortedDescending()

                // Log.d("DEBUG", "generateChart - availableMonths: $availableMonths")

                _chartUpdate.update {
                    it.copy(
                        availableMonths = availableMonths.ifEmpty { listOf(0) },
                        monthDayDataPoints = listMonthDaysPoints[availableMonths.firstOrNull()]
                            ?: zeroMonthDaysDataPoints
                    )
                }


            }

            ViewMode.Week -> {

                //{26 09 2025=401, 27 09 2025=222, 04 10 2025=86, 05 10 2025=30, 06 10 2025=10}

                val chartDataWeekDays = listDays
                    .map { it.get(weekOfYearField) }
                    .distinct()
                    .sortedDescending()
                    .associateWith { weekNumber ->
                        val startOfWeek = firstDayOfYear.with(weekOfYearField, weekNumber.toLong())
                            .with(dayOfWeekField, 1)

                        val endOfWeek = startOfWeek.plusDays(6)

                        startAndEndWeek.add(startOfWeek to endOfWeek)


                        Log.e("DEBUG", "generateChart - startAndEndWeek: $startAndEndWeek")

                        val datesInWeek = List(7) { dayOffset ->
                            startOfWeek.plusDays(dayOffset.toLong()).format(formatterDay)
                        }

                        datesInWeek.mapIndexed { index, date ->
                            Point(index.toFloat(), totalFocusOfADay[date]?.toFloat() ?: 0f)
                        }
                    }

                /*
                val duration: Duration = measureTime {
                    // Your code here
                    val chartDataWeekDays: Map<String, List<Point>> = buildMap {
                        listDays.map { it.get(weekOfYearField) }
                            .toSet()
                            .forEach { weekNumber ->
                                val firstWeekDate =
                                    firstDayOfYear.with(weekOfYearField, weekNumber.toLong())
                                //create a random date in a certain week, based on the given rule

                                val startOfWeek = firstWeekDate.with(dayOfWeekField, 1) // Monday
                                //from that, find the first day of the week based on the given week rule

                                val datesInWeek: List<String> = (1..7).map {
                                    startOfWeek.plusDays(it.toLong()).format(formatterDay)
                                }


                                put(weekNumber.toString(), datesInWeek)
                            }//this returns Map<String, List<String>>
                    }.mapValues { entry ->
                        entry.value.mapIndexed { index, date ->
                            Point(index.toFloat(), totalFocusOfADay[date]?.toFloat() ?: 0f)
                        }
                    }//transform string -> DataPoint
                }

                Log.d("DEBUG", "generateChart - chartDataWeekDays duration : $duration")
                */  // ~3ms


                updateChartState(chartDataWeekDays = chartDataWeekDays)

                Log.d(
                    "DEBUG",
                    "generateChart - chartDataWeekDays: ${chartState.value.chartDataWeekDays}"
                )

                val availableWeek: List<Int> = chartDataWeekDays.keys.toList().sortedDescending()


                //Log.d("DEBUG", "generateChart - availableWeek: $availableWeek")
                _chartUpdate.update {
                    it.copy(
                        availableWeeks = availableWeek.ifEmpty { listOf(0) },
                        weekDayDataPoints = chartDataWeekDays[availableWeek.firstOrNull()]
                            ?: zeroWeekDaysDataPoints,
                        startAndEndWeek = startAndEndWeek
                    )
                }
                Log.e(
                    "DEBUG",
                    "generateChart - startAndEndWeek: ${chartUpdate.value.startAndEndWeek}"
                )

            }

            ViewMode.Day -> {
                /*
                val chartDataDayHours: Map<String, List<Point>> = preferencesObject.asMap()
                    .filterKeys {
                        regexDayHourKey.matches(it.name)
                        //return a Map that only contains keys that matches the form : "29 09 2025T0"
                    }.toList()
                    .groupBy { it.first.name.substringBefore("T") }
                    //this will just return an empty Map if preferencesObject is empty
                    .mapValues { entry ->
                        create24HoursKey(entry.key, preferencesObject) }
                  */


                val chartDataDayHourRoom = monsterDataControllerImpl.getHourFocusData()
                    .groupBy { obj ->
                        obj.hour.substringBefore("T")
                    }.mapValues { entry ->
                        create24HoursKeyTest2(entry.key, entry.value)
                    }

                Log.e("ROOM", "generateChart - chartDataDayHourRomm: $chartDataDayHourRoom")


                updateChartState(chartDataDayHours = chartDataDayHourRoom)

                //Log.d("DEBUG", "generateChart - chartDataDayHours: ${chartState.value.chartDataDayHours}")

                val availableDays: List<String> = chartDataDayHourRoom.keys.toList()
                    .map { LocalDate.parse(it, formaterYearFirst) }
                    .sortedDescending()
                    .map {
                        it.format(formaterYearFirst)
                    }
                //  Log.d("DEBUG", "generateChart - availableDays: $availableDays")

                //the latest focus day is at index 0
                if (availableDays.isNotEmpty()) {
                    _chartUpdate.update {
                        it.copy(
                            availableDays = availableDays,
                            dateHourDataPoint = chartDataDayHourRoom.values.first()
                        )
                    }
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

    fun pickDay(viewMode: ViewMode = ViewMode.Day, leftOrRight: Boolean) {
        val availableDays: List<String> = chartUpdate.value.availableDays
        val availableWeeks: List<Int> = chartUpdate.value.availableWeeks
        val availableMonths: List<Int> = chartUpdate.value.availableMonths
        val availableYears: List<Int> = chartUpdate.value.availableYears

        when (viewMode) {
            ViewMode.Day -> {
                Log.d("DEBUG", "generateChart - dayIndex: ${chartUpdate.value.dayIndex}")
                if (chartUpdate.value.dayIndex < availableDays.size - 1 && chartUpdate.value.dayIndex >= 0) {
                    _chartUpdate.update {
                        it.copy(
                            dayIndex = if (leftOrRight) {
                                if (it.dayIndex == 0) 0 else
                                    it.dayIndex - 1
                            } else {
                                it.dayIndex + 1
                            }
                        )
                    }
                } else {
                    _chartUpdate.update {
                        it.copy(
                            dayIndex = 0
                        )
                    }
                }
                val dayIndex: Int = chartUpdate.value.dayIndex

                val day: String = availableDays.getOrNull(dayIndex) ?: "No data"
                _chartUpdate.update {
                    it.copy(
                        dateHourDataPoint = chartState.value.chartDataDayHours?.getOrDefault(
                            day,
                            zeroDayHoursDataPoints
                        ) ?: zeroDayHoursDataPoints
                    )
                }
            }

            ViewMode.Week -> {
                Log.d("DEBUG", "generateChart - weekIndex: ${chartUpdate.value.weekIndex}")
                if (chartUpdate.value.weekIndex < availableWeeks.size - 1 && chartUpdate.value.weekIndex >= 0) {
                    _chartUpdate.update {
                        it.copy(
                            weekIndex = if (leftOrRight) {
                                if (it.weekIndex == 0) 0 else
                                    it.weekIndex - 1
                            } else {
                                it.weekIndex + 1
                            }
                        )
                    }
                } else {
                    _chartUpdate.update {
                        it.copy(
                            weekIndex = 0
                        )
                    }
                }

                val weekIndex: Int = chartUpdate.value.weekIndex

                val week: Int = availableWeeks.getOrNull(weekIndex) ?: 0
                _chartUpdate.update {
                    it.copy(
                        weekDayDataPoints = chartState.value.chartDataWeekDays?.getOrDefault(
                            week,
                            zeroWeekDaysDataPoints
                        ) ?: zeroWeekDaysDataPoints
                    )
                }
            }

            ViewMode.Month -> {
                if (chartUpdate.value.monthIndex < availableMonths.size - 1 && chartUpdate.value.monthIndex >= 0) {
                    Log.d("DEBUG", "generateChart - monthIndex: ${chartUpdate.value.monthIndex}")
                    _chartUpdate.update {
                        it.copy(
                            monthIndex = if (leftOrRight) {
                                if (it.monthIndex == 0) 0 else
                                    it.monthIndex - 1
                            } else {
                                it.monthIndex + 1
                            }
                        )
                    }
                } else {
                    _chartUpdate.update {
                        it.copy(
                            monthIndex = 0
                        )
                    }
                }
                val monthIndex: Int = chartUpdate.value.monthIndex
                val month: Int = availableMonths.getOrNull(monthIndex) ?: 0
                _chartUpdate.update {
                    it.copy(
                        monthDayDataPoints = chartState.value.chartDataMonthDays?.getOrDefault(
                            month,
                            zeroMonthDaysDataPoints
                        ) ?: zeroMonthDaysDataPoints
                    )
                }
            }

            ViewMode.Year -> {
                Log.d("DEBUG", "generateChart - yearIndex: ${chartUpdate.value.yearIndex}")
                if (chartUpdate.value.yearIndex < availableYears.size - 1 && chartUpdate.value.yearIndex >= 0) {
                    _chartUpdate.update {
                        it.copy(
                            yearIndex = if (leftOrRight) {
                                if (it.yearIndex == 0) 0 else it.yearIndex - 1
                            } else {
                                it.yearIndex + 1
                            }
                        )
                    }
                } else {
                    _chartUpdate.update {
                        it.copy(
                            yearIndex = 0
                        )
                    }
                }
                val yearIndex: Int = chartUpdate.value.yearIndex
                val year: Int = availableYears.getOrNull(yearIndex) ?: 0
                _chartUpdate.update {
                    it.copy(
                        yearMonthDataPoints = chartState.value.chartDataYearMonths?.getOrDefault(
                            year,
                            zeroYearMonthsDataPoints
                        ) ?: zeroYearMonthsDataPoints
                    )
                }
            }
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

            Point(index.toFloat(), preferencesObject?.get(key)?.toFloat() ?: 0f) //Look up and get
        }
        return chartDataDay
    }


    private  fun create24HoursKeyTest2(
        dateString: String,
        list: List<MonsterFightingHourlyFocus>
    ): List<Point> {
        val chartDataDay = hourList.mapIndexed { index, hour ->
            val key = dateString.format(formatterDay) + "T" + hour.toString().padStart(2, '0')

            val converter = list.associate { it.hour to it.focusTime }

            Point(index.toFloat(), converter[key]?.toFloat() ?: 0f) //Look up and get
        }
        return chartDataDay
    }


    // Helper functions for better organization
    private fun updateChartState(
        chartDataYearMonths: Map<Int, List<Point>>? = null,
        chartDataYearWeeks: List<Point>? = null,
        chartDataYearDays: List<Point>? = null,
        chartDataWeekDays: Map<Int, List<Point>>? = null,
        chartDataMonthDays: Map<Int, List<Point>>? = null,
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



