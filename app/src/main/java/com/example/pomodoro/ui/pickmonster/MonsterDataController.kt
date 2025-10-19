package com.example.pomodoro.ui.pickmonster


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import co.yml.charts.common.model.Point
import com.example.pomodoro.R
import com.example.pomodoro.data.MonsterFightingDB
import com.example.pomodoro.data.MonsterFightingDao
import com.example.pomodoro.data.MonsterFightingHourlyFocus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


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

    suspend fun getHourFocusData(): List<MonsterFightingHourlyFocus>


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

    init{
        scope.launch {
            migrateHourFocusData()
        }
    }

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

    override suspend fun getHourFocusData(): List<MonsterFightingHourlyFocus> {
        return dao.getAllHourFocusData()
    }

    override suspend fun migrateHourFocusData() {

        val preferObj = dataStore.data.first()

        val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")
        Log.d("ROOM", "migrateHourFocusData1: ${dataStore.data.first()}")

        val filtered  = preferObj.asMap().filterKeys {
            regexDayHourKey.matches(it.name)
        }
        val focusList = filtered.map {
            val localDate: LocalDateTime = LocalDateTime.parse(it.key.name, DateTimeFormatter.ofPattern("dd MM yyyy'T'HH"))
            val converted: String = localDate.format(DateTimeFormatter.ofPattern("yyyy MM dd'T'HH"))

            Log.d("ROOM", "migrateHourFocusData: $converted")


            MonsterFightingHourlyFocus(
                hour = converted,
                focusTime = it.value.toString().toIntOrNull() ?: 0
            )
        }

        dataStore.edit { pref ->
            filtered.forEach {
                pref.remove(it.key)
            }
        } //delete every trace of hour focus data in dataStore.

        dao.insertAllHourFocusData(focusList)

        if(focusList.size == 1 || focusList.isEmpty()) {
            val lastHourData = focusList.lastOrNull()

            if (lastHourData != null) {
                dao.updateHourFocusTime(lastHourData.hour, lastHourData.focusTime)
            }
        }  else {
            val firstFocus = focusList.first()
            dao.updateHourFocusTime(firstFocus.hour, firstFocus.focusTime)
        }


        Log.d("ROOM", "migrateHourFocusData2: ${dataStore.data.first()}")

        Log.d("ROOM", "updateHourFocusTime - getAllHourFocusData: ${dao.getAllHourFocusData()}")
    }




    //call everytime users want to see statics


    override suspend fun getLatestById(): MonsterFightingDB? {
        return dao.getLatestById()
    }

}



























