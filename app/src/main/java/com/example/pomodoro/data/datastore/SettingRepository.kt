package com.example.pomodoro.data.datastore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class SettingsRepository @Inject constructor(
    private val dataStore : DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    //------------HOUR-----------//

    fun currentHourKey(): Preferences.Key<Int> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm")
        val hourKey = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).format(formatter)
        /*
        LocalDateTime.now() returns an instance of LocalDateTime with the current year, month, day, hour, minute, second, and nanosecond
        —based on the system clock.
        Then, truncatedTo(ChronoUnit.HOURS) removes the smaller time units (minute, second, nanosecond), leaving only the hour precision.

        Next, DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm") formats the truncated LocalDateTime into a string like "21-09-2025T14:00".

        Even though the formatter includes minutes (HH:mm), the truncated time has minute = 0, so the output shows "00" for minutes.
        */
        return intPreferencesKey(hourKey)
    }
    //this function returns : "21-09-2025T14:00" -> a key for current hour
    //create a key hourly


    suspend fun incrementFocusForCurrentHour(seconds: Int) {
        val key = currentHourKey()
        val old = dataStore.data.first()[key] ?: 0
        dataStore.edit { prefs ->
            prefs[key] = old + seconds
        }
    }
//save focus time hourly when called


    suspend fun getTodayHourlyFocus(): Map<Int, Int> {
        val today = LocalDate.now().toString()
        todayHourlyFocus = dataStore.data.map { prefs ->
            prefs.asMap()
                .filterKeys { it.name.startsWith(today) } //filter keys that start with today's date
                .map { (key, value) ->
                    val hour: Int = LocalDateTime.parse(key.name).hour
                    hour to (value as? Int ?: 0)
                    /*
                    * value as? Int?: 0 -> means that:
                    * see value as an Int value, if not -> returns null
                    * and if null -> returns 0
                    * this makes sure the value is always an Int, prevent the app from crashing.
                    * */
                }
                .toMap()
        }.first()
        return todayHourlyFocus
    }
    /*
    * this returns Flow of map like: flow(map(14 to 120, 15 to 180, 16 to 240, 17 to 300)) .....
    * */

   var todayHourlyFocus: Map<Int, Int> = emptyMap()



    //----------TODAY ----------------//

    //todayKey() function returns a key for today's date, everyday will have a different key

    fun  todayKey (): Preferences.Key<Int> {
        val key = LocalDate.now().toString() //today's date in string
        return intPreferencesKey(key)  // create a key for today's date - each day has a key for today's focus time
    }


/*
* incrementTodayFocusSeconds:
*  - take in a number of seconds (1)
* - create a key for today's day
* - create an old val to store the old value -> if haven't focused -> null -> old = 0
* - increase old by 1
* */

    suspend fun incrementTodayFocusSeconds(second: Int) {
        val key = todayKey() //today key
        val old = dataStore.data.first()[key] ?: 0
        dataStore.edit { prefs ->
            prefs[key] = old + second
        }
    } //save number of seconds focused today



    fun getTodayFocusMinutes(): Flow<Int> {
        val key = todayKey()
        return dataStore.data.map { prefs ->
            prefs[key] ?: 0
        }
    }
}