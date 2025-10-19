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



    @Query ("""
        SELECT strftime('%Y-%m-%d', datetime(timestampStart / 1000, 'unixepoch')) AS day,
              SUM(totalFocusTime) AS focusTime
        FROM monsterfightingdb
        WHERE timestampStart BETWEEN :start AND :end
        GROUP BY day
        ORDER BY focusTime DESC
        LIMIT 1
    """)
    suspend fun getMostFocusedDay(start: Long, end: Long): FocusSummary

    @Query("UPDATE MonsterFightingHourlyFocus SET focusTime = focusTime + :duration WHERE hour = :hour")
    suspend fun updateHourFocusTime(hour: String, duration: Int)

    @Query("SELECT * FROM MonsterFightingHourlyFocus ORDER BY hour DESC LIMIT 1")
    suspend fun getLatestHourById(): MonsterFightingHourlyFocus?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHourFocusData(data: List<MonsterFightingHourlyFocus>)




    @Query("SELECT * FROM MonsterFightingHourlyFocus")
    suspend fun getAllHourFocusData(): List<MonsterFightingHourlyFocus>


    @Query("DELETE FROM MonsterFightingHourlyFocus")
    suspend fun clearAllSessions()

    @Update
    suspend fun updateMonsterFightData(monsterFightingDB: MonsterFightingDB)

    @Insert
    suspend fun insertMonsterFightData(monsterFightingDB: MonsterFightingDB)

}


@Entity
data class MonsterFightingHourlyFocus (
    @PrimaryKey() val hour: String = "18 10 2025T16",
    val focusTime: Int = 0,
)


@Database (entities = [MonsterFightingDB::class, MonsterFightingHourlyFocus::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monsterFightingDao(): MonsterFightingDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS MonsterFightingHourlyFocus")
                db.execSQL("""
            CREATE TABLE MonsterFightingHourlyFocus (
                hour TEXT NOT NULL PRIMARY KEY,
                focusTime INTEGER NOT NULL
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

