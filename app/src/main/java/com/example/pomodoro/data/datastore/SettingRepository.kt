package com.example.pomodoro.data.datastore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore : DataStore<Preferences>
) {
    val TOTAL_FOCUS_SECONDS= intPreferencesKey("total_focus_seconds")

    suspend fun incrementFocusSeconds(seconds: Int) {
        val old = dataStore.data.first()[TOTAL_FOCUS_SECONDS] ?: 0
        dataStore.edit { prefs ->
            prefs[TOTAL_FOCUS_SECONDS] = old + seconds
        }
    }

    suspend fun getTotalFocusMinutes(): Int {
        val seconds: Int = dataStore.data.first()[TOTAL_FOCUS_SECONDS] ?: 0
        return seconds / 60 // Convert seconds to minutes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun  todayKey (): Preferences.Key<Int> {
        val key = LocalDate.now().toString()
        return intPreferencesKey(key)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun incrementTodayFocusSeconds(second: Int) {
        val key = todayKey()
        val old = dataStore.data.first()[key] ?: 0
        dataStore.edit { prefs ->
            prefs[key] = old + second
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayFocusMinutes(): Flow<Int> {
        val key = todayKey()
        return dataStore.data.map { prefs ->
            prefs[key] ?: 0
        }
    }

    suspend fun getAllFocusData(): Map<String, Any> { //type safety
        val prefs = dataStore.data.first()
        return prefs.asMap()
            .filterKeys { it.name.startsWith("focus_seconds_") }
            .mapKeys { it.key.name.removePrefix("focus_seconds_") }
    }


}