package com.example.pomodoro.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore : DataStore<Preferences>
) {
    val TOTAL_FOCUS_SECONDS_TODAY = intPreferencesKey("total_focus_seconds")

    suspend fun incrementFocusSeconds(seconds: Int) {
        val old = dataStore.data.first()[TOTAL_FOCUS_SECONDS_TODAY] ?: 0
        dataStore.edit { prefs ->
            prefs[TOTAL_FOCUS_SECONDS_TODAY] = old + seconds
        }
    }

    suspend fun getTotalFocusMinutes(): Int {
        val seconds = dataStore.data.first()[TOTAL_FOCUS_SECONDS_TODAY] ?: 0
        return seconds / 60
    }

}