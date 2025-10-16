package com.example.pomodoro.ui.pickmonster

import androidx.compose.ui.graphics.painter.Painter
import androidx.datastore.core.DataStore
import com.example.pomodoro.data.FocusSession
import com.example.pomodoro.data.FocusSessionDao
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

@Singleton
class MonsterDataControllerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dao: FocusSessionDao
) : MonsterDataController {

    var focusDuration: Int = 0

    fun tickFocus() = focusDuration++

    override fun saveMonsterFightingData(monster: String) {
        if(focusDuration >= 10) {//test for 10s
            val session = FocusSession(
                monsterName = monster,
                duration = focusDuration,
                timestamp = System.currentTimeMillis()
            )
        }
    }

}