package com.example.eduaituitor.data.database.dao

import androidx.room.*
import com.example.eduaituitor.data.LearningProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningProgressDao {
    @Query("SELECT * FROM learning_progress")
    fun getAllProgress(): Flow<List<LearningProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgress)

    @Update
    suspend fun updateProgress(progress: LearningProgress)
}
