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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


interface SettingsRepository {
    suspend fun createHourlyFocusKey(): Preferences.Key<Int>
    suspend fun saveHourlyFocusDuration(duration: Int)

}



data class ChartState (
    val chartData: List<DataPoint> = emptyList(),
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
        val todayKey: String? = LocalDate.now().format(formatterDay)
        val listTodayKey: MutableList<Preferences.Key<Int>> = mutableListOf()
        val listTodayDataPoint: MutableList<DataPoint> = mutableListOf()
        for ( key in keys) {
            listTodayKey.add(
                intPreferencesKey(name = if(key < 10 ) todayKey + "T" + "0" + key else todayKey + "T" + key)
            )
        }
        Log.d("DEBUG", "create24hoursKeys: $listTodayKey")
        for (key in listTodayKey) {
            listTodayDataPoint.add(
                DataPoint(
                    listTodayKey.indexOf(key).toFloat(),
                    dataStore.data.first()[key]?.toFloat() ?: 0f
                )
            )
        }
        _chartState.update{
            it.copy(
                chartData = listTodayDataPoint
            )
        }
        Log.d("DEBUG", "create24hoursKeys: ${chartState.value.chartData}")
    }
}
