package com.example.pomodoro.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

val Context.dataStore1 by preferencesDataStore("pomodoro_prefs")

object PomodoroKeys {
    val COMPLETED_SESSIONS = intPreferencesKey("completed_sessions")
    val TOTAL_FOCUS_MINUTES = intPreferencesKey("total_focus_minutes")
    val LAST_SAVED_DATE = stringPreferencesKey("last_saved_date")
}

class DataStoreVM (private val context: Context) {
    private val dataStore = context.dataStore1



}