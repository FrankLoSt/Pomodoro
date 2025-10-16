package com.example.pomodoro.ui.pickmonster

import androidx.datastore.core.DataStore
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton

interface MonsterDataController {
    fun saveMonsterData (monster: Triple<Int, String, String>)
}

@Singleton
class MonsterDataControllerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : MonsterDataController {

    override fun saveMonsterData(monster: Triple<Int, String, String>) {
        TODO("Not yet implemented")
    }

}