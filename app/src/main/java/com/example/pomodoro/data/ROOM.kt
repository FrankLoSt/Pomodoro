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
    @PrimaryKey(autoGenerate = true) val id: Int,
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




