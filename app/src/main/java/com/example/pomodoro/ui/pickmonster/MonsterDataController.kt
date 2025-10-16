package com.example.pomodoro.ui.pickmonster

import androidx.datastore.core.DataStore
import com.example.pomodoro.data.FocusSessionDao
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton

interface MonsterDataController {
    fun saveMonsterFightingData (monster: Triple<Int, String, String>)
}

@Singleton
class MonsterDataControllerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dao: FocusSessionDao
) : MonsterDataController {

    override fun saveMonsterFightingData(monster: Triple<Int, String, String>) {
    }

}