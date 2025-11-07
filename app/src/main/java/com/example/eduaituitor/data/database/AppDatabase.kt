package com.example.eduaituitor.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eduaituitor.data.LearningProgress
import com.example.eduaituitor.data.QuizSession
import com.example.eduaituitor.data.database.dao.LearningProgressDao
import com.example.eduaituitor.data.database.dao.QuizSessionDao

@Database(
    entities = [QuizSession::class, LearningProgress::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizSessionDao(): QuizSessionDao
    abstract fun learningProgressDao(): LearningProgressDao
}
