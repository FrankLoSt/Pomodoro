package com.example.pomodoro.ui.pickmonster

import androidx.compose.ui.graphics.painter.Painter
import androidx.datastore.core.DataStore

import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton

data class MonsterInfo (
    val image: Painter,
    val name: String,
    val description: String,
    )

interface MonsterDataController {
    fun saveMonsterFightingData (monster: String)
}
