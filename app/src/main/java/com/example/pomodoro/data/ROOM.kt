package com.example.pomodoro.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class FocusSession (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monsterName: String,
    val duration: Int,
    val timestamp: Long
)

@Dao
interface FocusSessionDao {
    @Insert
    suspend fun insert(session: FocusSession)

    @Query("SELECT * FROM FocusSession")
    suspend fun getAllSessions(): List<FocusSession>
}

@Database(entities = [FocusSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun FocusSessionDao(): FocusSessionDao
}



/*@Entity
data class FocusSessionInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monsterFight: String,
    val totalSession: Int,
    val sessionCompleted: Int,
    val totalFocusTimeSet: Int,
    val totalFocusTimeCompleted: Int,
    val totalRestTime: Int,
    val timestampSet: Long,
    val timestampFinished: Long?,
    val status: Boolean
)
*/


