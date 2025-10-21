package com.example.pomodoro.ui.pickmonster


import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.room.withTransaction
import com.example.pomodoro.R
import com.example.pomodoro.data.AppDatabase
import com.example.pomodoro.data.MonsterFightingDB
import com.example.pomodoro.data.MonsterFightingDao
import com.example.pomodoro.data.MonsterFightingHourlyFocus
import com.example.pomodoro.ui.statistics.TopMonsterData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import javax.inject.Inject
import javax.inject.Singleton







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

    suspend fun getTop10Monsters()


    fun updateMonsterPickedIndex(index: Int)

    suspend fun updateMostFocusedHour()

    suspend fun  updateMostFocusedDayOfMonth()

    suspend fun  updateMostFocusedDay()


    suspend fun  updateLeastFocusedDayOfMonth()

    suspend fun  updateLeastFocusedDay()


}



class InitSetUpStateHolder {
    private val _monsterState = MutableStateFlow(MonsterState())
    val monsterState: StateFlow<MonsterState> = _monsterState.asStateFlow()

    fun updateState(newState: MonsterState) { _monsterState.value = newState }
}




@Singleton
class MonsterDataControllerImpl @Inject constructor(
    val scope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val dao: MonsterFightingDao,
    private val initSetUpStateHolder: InitSetUpStateHolder
): MonsterDataController {

    val monsterState = initSetUpStateHolder.monsterState

    @Inject lateinit var db: AppDatabase
    suspend fun migrateData(listData: List<MonsterFightingHourlyFocus>) {
        db.withTransaction {
            val dao = db.monsterFightingDao()

            dao.insertAllHourFocusData(listData)
            val target = if (listData.size <= 1) listData.lastOrNull() else listData.first()
            target?.let {
                dao.updateHourFocusTime(it.date, it.focusTime)
            }
        }
    }


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


    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy'T'HH")
    val formatterYearFirst: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd'T'HH")

    val regexDayHourKey: Regex = Regex("""\d{2} \d{2} \d{4}T\d{2}""")

    val weekFields: WeekFields = WeekFields.of(DayOfWeek.MONDAY, 1)
    val dayOfWeekField: TemporalField = weekFields.dayOfWeek()
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
        val newState = monsterState.value.copy(monsterPickedIndex = index)
        initSetUpStateHolder.updateState(newState)
        Log.d("ROOM", "updateMonsterPickedIndex: $index")
    }



    fun toggleSetUpPopup() {
        initSetUpStateHolder.updateState(
            monsterState.value.copy(
                toggleSetUp =  !monsterState.value.toggleSetUp
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


    private fun findWeekDay (firstDay: DayOfWeek = DayOfWeek.MONDAY, date: LocalDateTime): Int {
        return date.get(dayOfWeekField)
    }

    override suspend fun migrateHourFocusData() {

        val preferObj = dataStore.data.first()
        
        Log.d("ROOM", "migrateHourFocusData1: $preferObj")

        val filtered  = preferObj.asMap().filterKeys {
            regexDayHourKey.matches(it.name)
        }

        val focusList = buildList {
            for ((prefKey, value) in preferObj.asMap()) {
                val keyName = prefKey.name
                if (!regexDayHourKey.matches(keyName)) continue

                // Avoid creating new formatter each loop — reuse precompiled
                val parsed = runCatching { LocalDateTime.parse(keyName, formatter) }.getOrNull() ?: continue

                val dayOfWeek = when (findWeekDay(date = parsed)) {
                    1 -> "Monday"
                    2 -> "Tuesday"
                    3 -> "Wednesday"
                    4 -> "Thursday"
                    5 -> "Friday"
                    6 -> "Saturday"
                    else -> "Sunday"
                }



                val converted = formatterYearFirst.format(parsed)
                val focusTime = value.toString().toIntOrNull() ?: 0
                add(MonsterFightingHourlyFocus(
                    date = converted,
                    focusTime = focusTime,
                    dayOfWeek = dayOfWeek,
                    dayOfMonth = parsed.dayOfMonth
                )
                )
            }
        }
        Log.d("ROOM", "migrateHourFocusData: $focusList")

        migrateData(focusList)

        Log.d("ROOM", "migrateHourFocusData: Upate done - clean start")

        dataStore.edit { pref ->
            filtered.forEach {
                pref.remove(it.key)
            }
        } //delete every trace of hour focus data in dataStore.
    }

    override suspend fun getTop10Monsters() {
        scope.launch {
            dao.getTop10Monsters().collect { top10 ->
                val newState = monsterState.value.copy(top10 = top10)
                initSetUpStateHolder.updateState(newState)
            }
        }
    }
    //call everytime users want to see statics



    override suspend fun getLatestById(): MonsterFightingDB? {
        return dao.getLatestById()
    }


    //------Most vs Least

    override suspend fun  updateMostFocusedHour() {

        scope.launch {
            val mostFocusHour = dao.getMostFocusedHour()
            mostFocusHour.collect{
                val newState = monsterState.value.copy(mostFocusedHour = it)
                initSetUpStateHolder.updateState(newState)
            }
        }
    }

    override suspend fun  updateMostFocusedDayOfMonth() {
        scope.launch {
            val mostFocusDayOfMonth = dao.getMostFocusedDayOfMonth()
            mostFocusDayOfMonth.collect {
                val newState = monsterState.value.copy(mostFocusedDayOfMonth = it)
                initSetUpStateHolder.updateState(newState)
            }
        }
    }

    override suspend fun  updateMostFocusedDay() {
        scope.launch {
            val mostFocusDay = dao.getMostFocusedDay()
            mostFocusDay.collect {
                val newState = monsterState.value.copy(mostFocusedDay = it)
                initSetUpStateHolder.updateState(newState)
            }
        }
    }

    override suspend fun  updateLeastFocusedDayOfMonth() {
        scope.launch {
            val leastFocusDayOfMonth = dao.getLeastFocusedDayOfMonth()
            leastFocusDayOfMonth.collect {
                val newState = monsterState.value.copy(leastFocusedDayOfMonth = it)
                initSetUpStateHolder.updateState(newState)
            }
        }
    }

    override suspend fun  updateLeastFocusedDay() {
        scope.launch {
            val leastFocusDay = dao.getLeastFocusedDay()
            leastFocusDay.collect {
                val newState = monsterState.value.copy(leastFocusedDay = it)
                initSetUpStateHolder.updateState(newState)
            }
        }
    }

}



























