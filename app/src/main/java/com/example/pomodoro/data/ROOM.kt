package com.example.pomodoro.data

import androidx.core.i18n.DateTimeFormatterSkeletonOptions
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
import com.example.pomodoro.ui.statistics.TopMonsterData
import kotlinx.coroutines.flow.Flow

data class FocusSummary(
    val day: String,
    val focusTime: Int
)

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

@Dao
interface MonsterFightingDao {
    @Query("SELECT * FROM MonsterFightingDB")
    suspend fun getAllMonsterFightData(): List<MonsterFightingDB>


    @Query("SELECT * FROM MonsterFightingDB ORDER BY id DESC LIMIT 1")
    suspend fun getLatestById(): MonsterFightingDB?

    @Query("SELECT SUM(totalFocusTime) FROM monsterfightingdb WHERE timestampStart BETWEEN :startDate AND :endDate" )
    suspend fun getSumOfFocusTime(startDate: Long, endDate: Long): Int



    @Query("UPDATE MonsterFightingHourlyFocus SET focusTime = focusTime + :duration WHERE date = :date")
    suspend fun updateHourFocusTime(date: String, duration: Int)



    @Query("SELECT * FROM MonsterFightingHourlyFocus ORDER BY date DESC LIMIT 1")
    suspend fun getLatestHourById(): MonsterFightingHourlyFocus?


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



    @Query("SELECT * FROM MonsterFightingHourlyFocus")
    suspend fun getAllHourFocusData(): List<MonsterFightingHourlyFocus>

    

    @Query("DELETE FROM MonsterFightingHourlyFocus")
    suspend fun clearAllSessions()


    @Query("""
    SELECT SUBSTR(date, INSTR(date, 'T') + 1) AS hourOnly,
           SUM(focusTime) AS totalFocus
    FROM MonsterFightingHourlyFocus
    GROUP BY hourOnly
    ORDER BY totalFocus DESC
    LIMIT 1
""")
    fun getMostFocusedHour(): String



    @Update
    suspend fun updateMonsterFightData(monsterFightingDB: MonsterFightingDB)

    @Insert
    suspend fun insertMonsterFightData(monsterFightingDB: MonsterFightingDB)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHourFocusData(data: List<MonsterFightingHourlyFocus>)
}


@Entity
data class MonsterFightingHourlyFocus (
    @PrimaryKey() val date: String = "2025 18 10T16",
    val focusTime: Int = 0,
    val dayOfWeek: String = "Monday",
    val dayOfMonth: Int = 1,
)


@Database (entities = [MonsterFightingDB::class, MonsterFightingHourlyFocus::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monsterFightingDao(): MonsterFightingDao

    companion object {
        //be careful, you need to save data before you change something. This is a hidden bom, not careful -> you screw things up
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE MonsterFightingHourlyFocus RENAME hour to date")
                db.execSQL("ALTER TABLE MonsterFightingHourlyFocus ADD COLUMN dayOfWeek TEXT NOT NULL DEFAULT 'Monday'")
                db.execSQL("ALTER TABLE MonsterFightingHourlyFocus ADD COLUMN dayOfMonth INTEGER NOT NULL DEFAULT '1'")
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

