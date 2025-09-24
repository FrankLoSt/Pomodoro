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
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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


    suspend fun fillMissingKeysWithRule(): GapResult
}

sealed class GapResult {
    object None : GapResult()                // no gap
    data class Filled(val hours: Int) : GapResult()  // gap ≤ 7 days, filled with zeros
    data class TooLong(val days: Long) : GapResult() // gap > 7 days, show message
}

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class SettingsRepositoryImpl @Inject constructor( //this tells Hilt that I need to inject this dependency in the constructor to build this class -> Hilt looks at it at compile time -> draw the graph -> then at run time -> it will inject the dependency
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : SettingsRepository {

    private val LAST_FOCUS_KEY = stringPreferencesKey("last_active_time")
    private val formatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH ")


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
            it[hourKey] = old + duration
            it[hourKey]?.let { it1 -> //
                if (it1 >= 10) {
                    it[LAST_FOCUS_KEY] = LocalDateTime.now().format(formatter).toString() //this is correct
                    Log.d("DEBUG", "Last Time Focus : ${it[LAST_FOCUS_KEY].toString()}")
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
            initialValue = "No recorded"
        ) //this only pull data, not affect anything
    }



    // Fill missing keys only if gap ≤ 7 days
    override suspend fun fillMissingKeysWithRule(): GapResult {
        Log.d("DEBUG", "fillMissingKeysWithRule: called")
        val now = LocalDateTime.now()
        //this is when things get complicated



        Log.d("DEBUG", "fillMissingKeysWithRule - now: $now")

        val prefs = dataStore.data.first() //most recent snap shot of dataStore -> it is a Flow<Preferences>

        val lastActiveRaw = prefs[LAST_FOCUS_KEY]  //pull lastActiveTime from dataStore -> it is a string

        Log.d("DEBUG", "fillMissingKeysWithRule - lastActiveRaw: $lastActiveRaw")

        val lastActive = lastActiveRaw?.let {
            LocalDateTime.parse(it)
        } ?: now
        //if lastActiveRaw is not null -> transform it into LocalDateTime object, if it is null -> set it to now

        val gapDays = ChronoUnit.DAYS.between(lastActive, now) // [1, 10) -> 9 days between lastActive and now
        //count whole days between last time recorded with now.

        return if (gapDays > 7) {
            Log.d("DEBUG", "fillMissingKeysWithRule - TooLong: $gapDays")
            // Don’t fill → return TooLong so UI can display a message
            GapResult.TooLong(gapDays)
        } else { //if dayDays <= 7, fill missing keys
            // Fill missing hours with zeros
            val missingKeys = mutableListOf<Preferences.Key<Int>>()
            var cursor = lastActive.plusHours(1)//original cursor is 1 hour after lastActive

            //looping until cursor is current time, if behide -> loop
            while (cursor.isBefore(now)) {
                val key = intPreferencesKey(cursor.format(formatter)) //create a key for each hour if cursor is before current time.
                if (prefs[key] == null) {
                    missingKeys.add(key)
                }
                cursor = cursor.plusHours(1) //plus one hour to cursor each loop
            }

            //if missingKeys is not empty -> fill empty hours with 0
            if (missingKeys.isNotEmpty()) {
                dataStore.edit { editPrefs ->
                    for (key in missingKeys) {
                        editPrefs[key] = 0
                    }
                }
            }
            // Update last active to now
            dataStore.edit { it[LAST_FOCUS_KEY] = now.toString() }

            GapResult.Filled(missingKeys.size)
        }
    }
}
