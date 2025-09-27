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
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


interface SettingsRepository {
    suspend fun createHourlyFocusKey(): Preferences.Key<Int>
    suspend fun saveHourlyFocusDuration(duration: Int)
   suspend fun saveWeekFocusDuration(duration: Int)
   suspend fun saveMonthFocusDuration(duration: Int)
   suspend fun saveYearFocusDuration(duration: Int)

    suspend fun saveAllFocusDurationData ()
}



data class ChartState (
    val chartDataYear: List<DataPoint> = emptyList(),
    val chartDataWeek: List<DataPoint> = emptyList(),
    val chartDataMonth: List<DataPoint> = emptyList(),
    val chartDataDay: List<DataPoint> = emptyList(),
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

    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

     fun createWeekKey ():  Preferences.Key<Int> {
        val weekKey =  LocalDateTime.now().year.toString() + "-" + LocalDateTime.now().get(WeekFields.ISO.weekOfYear()).toString()
         return intPreferencesKey(weekKey)
    }
    fun createMonthKey (): Preferences.Key<Int> {
        val monthKey = LocalDateTime.now().year.toString() + "-" + LocalDateTime.now().month.value.toString()
        return intPreferencesKey(monthKey)
    }
    fun createYearKey (): Preferences.Key<Int> {
        val yearKey: String = LocalDateTime.now().year.toString()
        if(yearKey.toInt() !in yearsList) {
            yearsList.add(LocalDateTime.now().year)
        }
        return intPreferencesKey(yearKey)
    }
    override suspend fun createHourlyFocusKey(): Preferences.Key<Int> {
        val hourKey = LocalDateTime.now().format(formatter)
        Log.d("DEBUG", "createHourlyFocusKey: $hourKey")
        return intPreferencesKey(hourKey)
    }
    //a helper function to create a key for each hour.
    override suspend fun saveWeekFocusDuration(duration: Int) {
        val weekKey = createWeekKey()
        val old = dataStore.data.first()[weekKey] ?: 0
        dataStore.edit {
            it[weekKey] = old + duration  //save in "39"
        }
        Log.d("DEBUG", " Week $weekKey: ${dataStore.data.first()[weekKey]}")
    }
    override suspend fun saveMonthFocusDuration(duration: Int) {
        val monthKey = createMonthKey()
        val old = dataStore.data.first()[monthKey] ?: 0
        dataStore.edit {
            it[monthKey] = old + duration  //save in "29 09"
        }
        Log.d("DEBUG", " Month $monthKey: ${dataStore.data.first()[monthKey]}")
    }
    override suspend fun saveYearFocusDuration(duration: Int) {
        val yearKey = createYearKey()
        val old = dataStore.data.first()[yearKey] ?: 0
        dataStore.edit {
            it[yearKey] = old + duration  //save in "29 09 2025"
        }
        Log.d("DEBUG", " Year $yearKey: ${dataStore.data.first()[yearKey]}")
    }

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
            } //only count when users focus more than 10 second

        }
    }

    override suspend fun saveAllFocusDurationData () {
        saveHourlyFocusDuration(1)
        saveWeekFocusDuration(1)
        saveMonthFocusDuration(1)
        saveYearFocusDuration(1)
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

    val hourList = (0..23).toList()

    val monthsList = (1..12).toList()

    val weeksList = (1..52).toList()

    val yearsList: MutableList<Int> = mutableListOf()



    suspend fun create24hoursKeys() {
        val todayKey = getTodayFormattedKey()
        val weekKey = createWeekKey()
        val monthKey = createMonthKey()
        val yearKey = createYearKey()
        val preferences = dataStore.data.first()

        val chartDataDay = buildList {
            hourList.forEachIndexed { index, hour ->
                val hourKey = createHourKey(todayKey, hour)
                val value = preferences[hourKey]?.toFloat() ?: 0f

                add(DataPoint(index.toFloat(), value))
            }
        }

        val chartDataWeek = buildList {
            weeksList.forEachIndexed { index, hour ->
                val hourKey = createHourKey(weekKey.name, hour)
                val value = preferences[hourKey]?.toFloat() ?: 0f
                add(DataPoint(index.toFloat(), value))
            }
        }

        val chartDataMonth = buildList {
            monthsList.forEachIndexed { index, hour ->
                val hourKey = createHourKey(monthKey.name, hour)
                val value = preferences[hourKey]?.toFloat() ?: 0f
                add(DataPoint(index.toFloat(), value))
            }
        }

        val ChartDataYear = buildList {
            yearsList.forEachIndexed { index, hour ->
                val hourKey = createHourKey(yearKey.name, hour)
                val value = preferences[hourKey]?.toFloat() ?: 0f
                add(DataPoint(index.toFloat(), value))
            }
        }
        updateChartState(
             chartDataDay =  chartDataDay,
            chartDataWeek = chartDataWeek,
            chartDataMonth = chartDataMonth,
            chartDataYear = ChartDataYear
        )
    }

    // Helper functions for better organization
    private fun getTodayFormattedKey(): String {
        return LocalDate.now().format(formatterDay)
    }

    private fun createHourKey(todayKey: String, hour: Int): Preferences.Key<Int> {
        val hourString = hour.toString().padStart(2, '0')
        return intPreferencesKey("${todayKey}T$hourString")
    }

    private fun updateChartState(
        chartDataDay: List<DataPoint>,
        chartDataWeek: List<DataPoint> = emptyList(),
        chartDataMonth: List<DataPoint> = emptyList(),
        chartDataYear: List<DataPoint> = emptyList(),
    ) {
        _chartState.update {
            it.copy(
                chartDataDay = chartDataDay,
                chartDataWeek = chartDataWeek,
                chartDataMonth = chartDataMonth,
                chartDataYear = chartDataYear,
            )
        }
        Log.d("DEBUG", "" +
                "Chart data updated: ${chartDataDay.size} points" +
                " ${chartDataWeek.size} points" +
                " ${chartDataMonth.size} points" +
                " ${chartDataYear.size} points"
                )
    }

    fun trackWeekYear () {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek  // e.g., MONDAY, TUESDAY
        val dayName = dayOfWeek.name     // "MONDAY"
        val dayIndex = dayOfWeek.value   // 1 (Monday) to 7 (Sunday)
        val month = today.month.name
        val year = today.year
        val weekOfYear = today.get(WeekFields.ISO.weekOfYear())  // e.g., 39
        Log.d("DEBUG", "trackweekYear: $dayName, $dayIndex, $weekOfYear")
    } //this is only for testing

}



@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class SettingsRepositoryImpl2 @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : SettingsRepository {

    private val LAST_FOCUS_KEY = stringPreferencesKey("last_active_time")
    private val formatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")
    private val formatterDay = DateTimeFormatter.ofPattern("dd MM yyyy")

    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    private val hourList = (0..23).toList()
    private val monthsList = (1..12).toList()
    private val weeksList = (1..52).toList()
    private val yearsList = mutableSetOf<Int>()

    // Key Generators
    private fun now() = LocalDateTime.now()

    private fun createWeekKey(): Preferences.Key<Int> {
        val key = "${now().year}-${now().get(WeekFields.ISO.weekOfYear())}"
        return intPreferencesKey(key)
    }

    private fun createMonthKey(): Preferences.Key<Int> {
        val key = "${now().year}-${now().monthValue}"
        return intPreferencesKey(key)
    }

    private fun createYearKey(): Preferences.Key<Int> {
        val year = now().year
        yearsList.add(year)
        return intPreferencesKey(year.toString())
    }

    override suspend fun createHourlyFocusKey(): Preferences.Key<Int> {
        val key = now().format(formatter)
        Log.d("DEBUG", "createHourlyFocusKey: $key")
        return intPreferencesKey(key)
    }

    // Save Methods
    override suspend fun saveHourlyFocusDuration(duration: Int) {
        val key = createHourlyFocusKey()
        val preferences = dataStore.data.first()
        val old = preferences[key] ?: 0

        dataStore.edit {
            it[key] = old + duration
            if ((it[key] ?: 0) >= 10) {
                it[LAST_FOCUS_KEY] = now().format(formatter)
            }
        }
    }

    override suspend fun saveWeekFocusDuration(duration: Int) {
        saveDuration(createWeekKey(), duration)
    }

    override suspend fun saveMonthFocusDuration(duration: Int) {
        saveDuration(createMonthKey(), duration)
    }

    override suspend fun saveYearFocusDuration(duration: Int) {
        saveDuration(createYearKey(), duration)
    }

    override suspend fun saveAllFocusDurationData() {
        saveHourlyFocusDuration(1)
        saveWeekFocusDuration(1)
        saveMonthFocusDuration(1)
        saveYearFocusDuration(1)
    }

    private suspend fun saveDuration(key: Preferences.Key<Int>, duration: Int) {
        val preferences = dataStore.data.first()
        val old = preferences[key] ?: 0
        dataStore.edit { it[key] = old + duration }
    }

    // Chart Data
    suspend fun create24HoursKeys() {
        val preferences = dataStore.data.first()
        val todayKey = getTodayFormattedKey()
        val weekKey = createWeekKey().name
        val monthKey = createMonthKey().name
        val yearKey = createYearKey().name

        val chartDataDay = hourList.mapIndexed { index, hour ->
            val key = createHourKey(todayKey, hour)
            DataPoint(index.toFloat(), preferences[key]?.toFloat() ?: 0f)
        }

        val chartDataWeek = weeksList.mapIndexed { index, week ->
            val key = createHourKey(weekKey, week)
            DataPoint(index.toFloat(), preferences[key]?.toFloat() ?: 0f)
        }

        val chartDataMonth = monthsList.mapIndexed { index, month ->
            val key = createHourKey(monthKey, month)
            DataPoint(index.toFloat(), preferences[key]?.toFloat() ?: 0f)
        }

        val chartDataYear = yearsList.mapIndexed { index, year ->
            val key = createHourKey(yearKey, year)
            DataPoint(index.toFloat(), preferences[key]?.toFloat() ?: 0f)
        }

        updateChartState(chartDataDay, chartDataWeek, chartDataMonth, chartDataYear)
    }

    private fun getTodayFormattedKey(): String = LocalDate.now().format(formatterDay)

    private fun createHourKey(base: String, unit: Int): Preferences.Key<Int> {
        val padded = unit.toString().padStart(2, '0')
        return intPreferencesKey("${base}T$padded")
    }

    private fun updateChartState(
        chartDataDay: List<DataPoint>,
        chartDataWeek: List<DataPoint>,
        chartDataMonth: List<DataPoint>,
        chartDataYear: List<DataPoint>
    ) {
        _chartState.update {
            it.copy(
                chartDataDay = chartDataDay,
                chartDataWeek = chartDataWeek,
                chartDataMonth = chartDataMonth,
                chartDataYear = chartDataYear
            )
        }
        Log.d("DEBUG", "Chart updated: Day=${chartDataDay.size}, Week=${chartDataWeek.size}, Month=${chartDataMonth.size}, Year=${chartDataYear.size}")
    }

    fun getLastDayActive(): StateFlow<String?> {
        return dataStore.data.map { it[LAST_FOCUS_KEY] }
            .stateIn(scope, SharingStarted.WhileSubscribed(), null)
    }

    fun trackWeekYear() {
        val today = LocalDate.now()
        val dayName = today.dayOfWeek.name
        val dayIndex = today.dayOfWeek.value
        val weekOfYear = today.get(WeekFields.ISO.weekOfYear())
        Log.d("DEBUG", "trackWeekYear: $dayName ($dayIndex), Week $weekOfYear")
    }
}

