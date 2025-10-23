package com.example.pomodoro.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pomodoro.ui.pickmonster.FocusedDay
import com.example.pomodoro.ui.pickmonster.FocusedDayOfMonth
import com.example.pomodoro.ui.pickmonster.FocusedHour
import com.example.pomodoro.ui.pickmonster.MonsterInfo
import com.example.pomodoro.ui.pickmonster.SkillInfo
import com.example.pomodoro.ui.pickmonster.WeaponInfo
import com.example.pomodoro.ui.statistics.TopMonsterData
import kotlinx.coroutines.flow.Flow

//store users focus data used for report
@Entity
data class MonsterFightingDB (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monsterName: String = "Distraction",
    val totalSessions: Int = 0,
    val sessionsCompleted: Int = 0,
    val totalFocusTime: Int = 0,
    val totalRestTime: Int = 0,
    val timestampStart: Long = 0L,
    val timestampEnd: Long = 0L,
    val status: Boolean = false,
    val tag: String = "Study"
    )

//store user focus data by hour unit -> used for chart
@Entity
data class MonsterFightingHourlyFocus (
    @PrimaryKey() val date: String = "2025 18 10T16",
    val focusTime: Int = 0,
    val dayOfWeek: String = "Monday",
    val dayOfMonth: Int = 1,
)



//store users' monster items

@Entity(indices = [androidx.room.Index(value = ["itemType", "itemName"], unique = true)])
data class MonsterItemsStorage(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val itemType: String,
    val itemName: String,
    val itemDescription: String,
    val itemImage: Int,
    val isFavorite: Boolean = false,
)

@Entity
data class ROOMTags (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tag: String,
    val color: String,
)




//this will do all sorts of things with users' focus data.
@Dao
interface MonsterFightingDao {
    // ------------------------------MonsterFightingDb -------------------------------
    @Query("SELECT * FROM MonsterFightingDB")
    suspend fun getAllMonsterFightData(): List<MonsterFightingDB>


    @Query("SELECT * FROM MonsterFightingDB ORDER BY id DESC LIMIT 1")
    suspend fun getLatestById(): MonsterFightingDB?

    @Query("SELECT SUM(totalFocusTime) FROM monsterfightingdb WHERE timestampStart BETWEEN :startDate AND :endDate" )
    suspend fun getSumOfFocusTime(startDate: Long, endDate: Long): Int

    @Query("""
    SELECT
    monsterName,
        SUM(totalFocusTime) AS totalTime,
        SUM(totalSessions) AS totalSessions,
        SUM(sessionsCompleted) AS totalWins,
        SUM(totalSessions - sessionsCompleted) AS totalLoses
        FROM monsterfightingdb
        GROUP BY monsterName
    ORDER BY totalTime DESC
""")
    fun getTop10Monsters(): Flow<List<TopMonsterData>>

    @Update
    suspend fun updateMonsterFightData(monsterFightingDB: MonsterFightingDB)

    @Insert
    suspend fun insertMonsterFightData(monsterFightingDB: MonsterFightingDB)




    // --------------------------MonsterFightingHourlyFocus-----------------------------

    @Query("SELECT * FROM MonsterFightingHourlyFocus ORDER BY date DESC LIMIT 1")
    suspend fun getLatestHourById(): MonsterFightingHourlyFocus?





    @Query("SELECT * FROM MonsterFightingHourlyFocus")
    suspend fun getAllHourFocusData(): List<MonsterFightingHourlyFocus>

    

    @Query("DELETE FROM MonsterFightingHourlyFocus")
    suspend fun clearAllSessions()



    @Query("""
    SELECT SUBSTR(date, INSTR(date, 'T') + 1) AS hourOnly,
           SUM(focusTime) AS avgTime
    FROM MonsterFightingHourlyFocus
    GROUP BY hourOnly
    ORDER BY avgTime DESC
    LIMIT 1
""")
    fun getMostFocusedHour(): Flow<FocusedHour>


    @Query("UPDATE MonsterFightingHourlyFocus SET focusTime = focusTime + :duration WHERE date = :date")
    suspend fun updateHourFocusTime(date: String, duration: Int)

    @Query("""
        SELECT
              dayOfWeek, 
              SUM(focusTime) AS avgTime
        FROM MonsterFightingHourlyFocus
        GROUP BY dayOfWeek
        ORDER BY avgTime DESC
        LIMIT 1
    """)
    fun getMostFocusedDay(): Flow<FocusedDay>




    @Query("""
        SELECT 
        dayOfMonth, 
        SUM(focusTime) AS avgTime
        FROM MonsterFightingHourlyFocus
        GROUP BY dayOfMonth
        ORDER BY avgTime DESC
        LIMIT 1
    """)
    fun getMostFocusedDayOfMonth(): Flow<FocusedDayOfMonth>



    @Query("""
        SELECT
              dayOfWeek, 
              SUM(focusTime) AS avgTime
        FROM MonsterFightingHourlyFocus
        GROUP BY dayOfWeek
        ORDER BY avgTime ASC
        LIMIT 1
    """)
    fun getLeastFocusedDay(): Flow<FocusedDay>


    @Query("""
        SELECT 
        dayOfMonth, 
        SUM(focusTime) AS avgTime
        FROM MonsterFightingHourlyFocus
        GROUP BY dayOfMonth
        ORDER BY avgTime ASC
        LIMIT 1
    """)
    fun getLeastFocusedDayOfMonth(): Flow<FocusedDayOfMonth>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHourFocusData(data: List<MonsterFightingHourlyFocus>)
}


//this will do all sorts of things with monster Item
@Dao
interface MonsterItemsDao {

    @Query("SELECT * FROM MonsterItemsStorage")
    suspend fun getAllItems(): List<MonsterItemsStorage>

    @Query("SELECT * FROM ROOMTags")
    suspend fun getAllTags() : List<ROOMTags>

}



@Database (entities = [MonsterFightingDB::class, MonsterFightingHourlyFocus::class, MonsterItemsStorage::class, ROOMTags::class], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monsterFightingDao(): MonsterFightingDao

    abstract fun monsterItemsDao(): MonsterItemsDao


    companion object {
        //be careful, you need to save data before you change something. This is a hidden bom, not careful -> you screw things up
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS MonsterItemsStorage(
                          id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                          itemType TEXT NOT NULL, 
                          itemName TEXT NOT NULL, 
                          itemDescription TEXT NOT NULL, 
                          itemImage INTEGER NOT NULL, 
                          isFavorite INTEGER NOT NULL DEFAULT  0
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_MonsterItemsStorage_itemType_itemName ON MonsterItemsStorage (itemType, itemName)
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS ROOMTags(
                          id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                          tag TEXT NOT NULL, 
                          color TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }
    }
}
//This is a blueprint for a table in ROOM, that has:
// 10 fields
// monsterFightingDao returns an MonsterFightingDao object that can be used to interact with the table.
//so, you have the table, you have to tools to work with the data.
//And what you just did in the AppModule -> you just tell Hilt to create the real house for you whenever you call it in the app, the best part is that
//it is always the same house everywhere.

