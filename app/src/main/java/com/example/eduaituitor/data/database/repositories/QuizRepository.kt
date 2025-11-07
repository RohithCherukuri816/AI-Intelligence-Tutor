package com.example.eduaituitor.data.database.repositories

import com.example.eduaituitor.data.QuizSession
import com.example.eduaituitor.data.database.dao.QuizSessionDao
import kotlinx.coroutines.flow.Flow

class QuizRepository(private val quizSessionDao: QuizSessionDao) {
    fun getAllSessions(): Flow<List<QuizSession>> = quizSessionDao.getAllSessions()

    suspend fun saveSession(session: QuizSession) {
        quizSessionDao.insertSession(session)
    }
}
