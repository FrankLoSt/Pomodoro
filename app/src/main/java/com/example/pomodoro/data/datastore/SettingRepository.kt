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

    val keys = (0..23).toList()


    suspend fun create24hoursKeys() {
        val todayKey = getTodayFormattedKey()
        val preferences = dataStore.data.first()

        val chartDataDay = buildList {
            keys.forEachIndexed { index, hour ->
                val hourKey = createHourKey(todayKey, hour)
                val value = preferences[hourKey]?.toFloat() ?: 0f

                add(DataPoint(index.toFloat(), value))
            }
        }

        updateChartState(chartDataDay)
    }

    // Helper functions for better organization
    private fun getTodayFormattedKey(): String {
        return LocalDate.now().format(formatterDay)
    }

    private fun createHourKey(todayKey: String, hour: Int): Preferences.Key<Int> {
        val hourString = hour.toString().padStart(2, '0')
        return intPreferencesKey("${todayKey}T$hourString")
    }

    private fun updateChartState(chartData: List<DataPoint>) {
        _chartState.update { it.copy(chartDataDay = chartData) }
        Log.d("DEBUG", "Chart data updated: ${chartData.size} points")
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
