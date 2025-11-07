package com.example.eduaituitor.data.database.repositories

import com.example.eduaituitor.data.LearningProgress
import com.example.eduaituitor.data.database.dao.LearningProgressDao
import kotlinx.coroutines.flow.Flow

class ProgressRepository(private val progressDao: LearningProgressDao) {
    fun getAllProgress(): Flow<List<LearningProgress>> = progressDao.getAllProgress()

    suspend fun updateProgress(progress: LearningProgress) {
        progressDao.updateProgress(progress)
    }
}
