package com.example.eduaituitor.data.database.dao

import androidx.room.*
import com.example.eduaituitor.data.QuizSession
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizSessionDao {
    @Query("SELECT * FROM quiz_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<QuizSession>>

    @Insert
    suspend fun insertSession(session: QuizSession)

    @Delete
    suspend fun deleteSession(session: QuizSession)
}
