package com.example.pomodoro.ui.pickmonster

import android.util.Log
import androidx.compose.ui.graphics.painter.Painter
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit


import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.pomodoro.R
import com.example.pomodoro.data.MonsterFightingDB
import com.example.pomodoro.data.MonsterFightingDao
import com.example.pomodoro.data.MonsterFightingHourlyFocus
import com.example.pomodoro.ui.countdown.FocusUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext


data class MonsterInfo (
    val imageId: Int,
    val name: String,
    val description: String,
    )




interface MonsterDataController {
    fun saveMonsterFightingData (monster: String)

    suspend fun saveTick(duration: Int): Int

    suspend fun updateMonsterFightData(monsterFightingDB: MonsterFightingDB)

    suspend fun getAllMonsterFightData(): List<MonsterFightingDB>
    suspend fun insertMonsterFightData(monsterFightingDB: MonsterFightingDB)

    suspend fun getLatestById(): MonsterFightingDB?

    suspend fun getSumOfFocusTime(startDate: Long, endDate: Long): Int

    suspend fun getMostFocusedDay(start: Long, end: Long): String

    suspend fun migrateHourFocusData()

    fun updateMonsterPickedIndex(index: Int)

}



class InitSetUpStateHolder {
    private val _initSetUpState = MutableStateFlow(InitSetUpState())
    val initSetUpState: StateFlow<InitSetUpState> = _initSetUpState

    fun updateState(newState: InitSetUpState) {
        _initSetUpState.value = newState
    }
}




@Singleton
class MonsterDataControllerImpl @Inject constructor(
    val scope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val dao: MonsterFightingDao,
    private val initSetUpStateHolder: InitSetUpStateHolder
): MonsterDataController {


    val initSetUpState = initSetUpStateHolder.initSetUpState



    val monsterList: List<MonsterInfo> = listOf(
        MonsterInfo(
            R.drawable.warrior2,
            "Uncontrolled rage",
            "make you make impulsive and regretful decisions, breaking your relationships with your beloved people"
        ),
        MonsterInfo(imageId = R.drawable.treemonster, "Bed rot", "rotting your future and health"),
        MonsterInfo(R.drawable.spider, "Anxiety", "Makes everyday tasks feel overwhelming"),
        MonsterInfo(
            R.drawable.monster1,
            "Porn addiction",
            "Drain your energy and destroy your relationship"
        ),
        MonsterInfo(R.drawable._01_1, "Anxiety", "Makes everyday tasks feel overwhelming"),
        MonsterInfo(R.drawable._01_2, "Loneliness", "Leads to isolation and low self-worth"),
        MonsterInfo(R.drawable._02_2, "Burnout", "Kills motivation and joy in learning"),
        MonsterInfo(R.drawable._03_2, "Comparison", "Breeds insecurity through social media"),
        MonsterInfo(R.drawable._04_1, "Rejection", "Shakes confidence and self-image"),
        MonsterInfo(R.drawable._03_3, "Pressure", "Creates fear of failure and perfectionism"),
        MonsterInfo(R.drawable._06_2, "Procrastination", "Delays growth and builds guilt"),
        MonsterInfo(R.drawable._07_2, "Identity", "Confuses self-understanding and belonging"),
        MonsterInfo(R.drawable._08_2, "Addiction", "Distracts from goals and relationships"),
        MonsterInfo(R.drawable._09_2, "Bullying", "Damages trust and emotional safety"),
        MonsterInfo(R.drawable._10_2, "Self-Doubt", "Blocks ambition and creativity"),
        MonsterInfo(R.drawable._12_1, "Financial Stress", "Limits opportunity and causes anxiety"),
        MonsterInfo(R.drawable._07_3, "Overthinking", "Paralyzes decision-making"),
        MonsterInfo(R.drawable._13_2, "Imposter", "Makes success feel undeserved"),
        MonsterInfo(R.drawable._08_3, "Neglect", "Leaves emotional needs unmet"),
        MonsterInfo(R.drawable._11_1, "Fear", "Prevents risk-taking and growth"),
        MonsterInfo(R.drawable._14_1, "Toxic Positivity", "Invalidates real emotions"),
        MonsterInfo(R.drawable._15_1, "Distraction", "Scatters focus and productivity"),
        MonsterInfo(R.drawable._16_3, "Insecurity", "Erodes confidence and self-love"),
        MonsterInfo(R.drawable._14_3, "Perfectionism", "Turns effort into self-criticism"),
        MonsterInfo(R.drawable._18_2, "Isolation", "Disconnects from support systems"),
        MonsterInfo(R.drawable._19_2, "Uncertainty", "Creates anxiety about the future"),
        MonsterInfo(R.drawable._20_1, "Regret", "Chains you to the past")
    )



    val sessionKey: Preferences.Key<Int> = intPreferencesKey("session")

    override suspend fun saveTick(duration: Int): Int {
        dataStore.edit {
            val old: Int = it[sessionKey] ?: 0
            val newVal = old + 1
            it[sessionKey] = newVal
        }
        Log.d("ROOM", "saveTick: ${dataStore.data.first()[sessionKey]}")
        return dataStore.data.first()[sessionKey] ?:0
    }
    //saveTick does 2 jobs => increase + return / Be careful





    override fun updateMonsterPickedIndex(index: Int) {
        val newState = initSetUpState.value.copy(monsterPickedIndex = index)
        initSetUpStateHolder.updateState(newState)
        Log.d("ROOM", "updateMonsterPickedIndex: $index")
    }



    fun toggleSetUpPopup() {
        initSetUpStateHolder.updateState(
            initSetUpState.value.copy(
                toggleSetUp =  !initSetUpState.value.toggleSetUp
            )
        )
    }

    override suspend fun getSumOfFocusTime(startDate: Long, endDate: Long): Int {
        return dao.getSumOfFocusTime(startDate, endDate)
    } //Count total focus time in days, weeks, months, years :)))

    override suspend fun getAllMonsterFightData(): List<MonsterFightingDB> {
        Log.d("ROOM", "getAllMonsterFightData: called")
        return dao.getAllMonsterFightData()
    }

    override suspend fun updateMonsterFightData(monsterFightingDB: MonsterFightingDB) {
        dao.updateMonsterFightData(monsterFightingDB)
        dataStore.edit {
            it[sessionKey] = 0
        }
    }

    override suspend fun insertMonsterFightData(monsterFightingDB: MonsterFightingDB) {
        Log.d("ROOM", "insertMonsterFightData: called")
        dao.insertMonsterFightData(monsterFightingDB)
        dataStore.edit {
            it[sessionKey] = 0
        }
    }

    override fun saveMonsterFightingData(monster: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getMostFocusedDay(start: Long, end: Long): String {
        return "test"
    }

    val preferencesObj: Preferences = preferencesOf(
        intPreferencesKey("26 09 2025T15") to 10,
        stringPreferencesKey("last_active_time") to "06 10 2025T08",
        intPreferencesKey("26 09 2025T16") to 30,
        intPreferencesKey("26 09 2025T18") to 118,
        intPreferencesKey("26 09 2025T19") to 52,
        intPreferencesKey("26 09 2025T20") to 129,
        intPreferencesKey("26 09 2025T21") to 62,
        intPreferencesKey("27 09 2025T00") to 110,
        intPreferencesKey("27 09 2025T01") to 65,
        intPreferencesKey("27 09 2025T02") to 10,
        intPreferencesKey("27 09 2025T03") to 16,
        intPreferencesKey("27 09 2025T13") to 11,
        intPreferencesKey("27 09 2025T14") to 10,
        intPreferencesKey("04 10 2025T08") to 4,
        intPreferencesKey("04 10 2025T15") to 28,
        intPreferencesKey("04 10 2025T16") to 34,
        intPreferencesKey("04 10 2025T20") to 20,
        intPreferencesKey("05 10 2025T20") to 30,
        intPreferencesKey("06 10 2025T08") to 10,

        // October continued
        intPreferencesKey("07 10 2025T09") to 45,
        intPreferencesKey("08 10 2025T10") to 60,
        intPreferencesKey("09 10 2025T11") to 75,
        intPreferencesKey("10 10 2025T12") to 90,
        intPreferencesKey("11 10 2025T13") to 105,
        intPreferencesKey("12 10 2025T14") to 120,
        intPreferencesKey("13 10 2025T15") to 135,
        intPreferencesKey("14 10 2025T16") to 150,

        // November
        intPreferencesKey("01 11 2025T08") to 20,
        intPreferencesKey("02 11 2025T09") to 25,
        intPreferencesKey("03 11 2025T10") to 30,
        intPreferencesKey("04 11 2025T11") to 35,
        intPreferencesKey("05 11 2025T12") to 40,
        intPreferencesKey("06 11 2025T13") to 45,
        intPreferencesKey("07 11 2025T14") to 50,
        intPreferencesKey("08 11 2025T15") to 55,

        // December
        intPreferencesKey("01 12 2025T08") to 60,
        intPreferencesKey("02 12 2025T09") to 65,
        intPreferencesKey("03 12 2025T10") to 70,
        intPreferencesKey("04 12 2025T11") to 75,
        intPreferencesKey("05 12 2025T12") to 80,
        intPreferencesKey("06 12 2025T13") to 85,
        intPreferencesKey("07 12 2025T14") to 90,
        intPreferencesKey("08 12 2025T15") to 95,

        // January
        intPreferencesKey("10 01 2025T08") to 100,
        intPreferencesKey("11 01 2025T09") to 105,
        intPreferencesKey("12 01 2025T10") to 110,
        intPreferencesKey("13 01 2025T11") to 115,
        intPreferencesKey("14 01 2025T12") to 120,
        intPreferencesKey("15 01 2025T13") to 125,
        intPreferencesKey("16 01 2025T14") to 130,
        intPreferencesKey("17 01 2025T15") to 135,
        intPreferencesKey("18 01 2025T08") to 140,
        intPreferencesKey("19 01 2025T09") to 145,
        intPreferencesKey("20 01 2025T10") to 150,
        intPreferencesKey("21 01 2025T11") to 155,
        intPreferencesKey("22 01 2025T12") to 160,
        intPreferencesKey("23 01 2025T13") to 165,
        intPreferencesKey("24 01 2025T14") to 170,
        intPreferencesKey("25 01 2025T15") to 175,

        intPreferencesKey("01 02 2025T08") to 180,
        intPreferencesKey("02 02 2025T09") to 185,
        intPreferencesKey("03 02 2025T10") to 190,
        intPreferencesKey("04 02 2025T11") to 195,
        intPreferencesKey("05 02 2025T12") to 200,
        intPreferencesKey("06 02 2025T13") to 205,
        intPreferencesKey("07 02 2025T14") to 210,
        intPreferencesKey("08 02 2025T15") to 215,

        intPreferencesKey("15 03 2025T08") to 220,
        intPreferencesKey("16 03 2025T09") to 225,
        intPreferencesKey("17 03 2025T10") to 230,
        intPreferencesKey("18 03 2025T11") to 235,
        intPreferencesKey("19 03 2025T12") to 240,
        intPreferencesKey("20 03 2025T13") to 245,
        intPreferencesKey("21 03 2025T14") to 250,
        intPreferencesKey("22 03 2025T15") to 240,

        intPreferencesKey("01 04 2025T08") to 20,
        intPreferencesKey("02 04 2025T09") to 25,
        intPreferencesKey("03 04 2025T10") to 30,
        intPreferencesKey("04 04 2025T11") to 35,
        intPreferencesKey("05 04 2025T12") to 40,
        intPreferencesKey("06 04 2025T13") to 45,
        intPreferencesKey("07 04 2025T14") to 50,
        intPreferencesKey("08 04 2025T15") to 55,


        intPreferencesKey("10 05 2025T08") to 60,
        intPreferencesKey("11 05 2025T09") to 65,
        intPreferencesKey("12 05 2025T10") to 70,
        intPreferencesKey("13 05 2025T11") to 75,
        intPreferencesKey("14 05 2025T12") to 80,
        intPreferencesKey("15 05 2025T13") to 85,
        intPreferencesKey("16 05 2025T14") to 90,
        intPreferencesKey("17 05 2025T15") to 95,

        intPreferencesKey("20 06 2025T08") to 100,
        intPreferencesKey("21 06 2025T09") to 105,
        intPreferencesKey("22 06 2025T10") to 110,
        intPreferencesKey("23 06 2025T11") to 115,
        intPreferencesKey("24 06 2025T12") to 120,
        intPreferencesKey("25 06 2025T13") to 125,
        intPreferencesKey("26 06 2025T14") to 130,
        intPreferencesKey("27 06 2025T15") to 135,

        )

    override suspend fun migrateHourFocusData() {
        val preferObj = preferencesObj

        val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")

        preferObj.asMap().filterKeys {
            regexDayHourKey.matches(it.name)
        }.forEach {
            val new = MonsterFightingHourlyFocus(
                hour = it.key.name,
                focusTime = it.value.toString().toInt()
            )
            dao.insertHourFocusData(new)
        }

        Log.d("ROOM", "updateHourFocusTime - getAllHourFocusData: ${dao.getAllHourFocusData()}")
    }


    override suspend fun getLatestById(): MonsterFightingDB? {
        return dao.getLatestById()
    }

}



























