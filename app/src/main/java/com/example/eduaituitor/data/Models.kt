package com.example.eduaituitor.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Data models for the EduAI Tutor application
 */

// Chat message model
data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val isUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val topic: String = "",
    val edited: Boolean = false
)

// Quiz question model
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int, // index of correct option
    val userAnswer: Int = -1 // -1 means not answered
)

// Quiz session model
@Entity(tableName = "quiz_sessions")
data class QuizSession(
    @PrimaryKey val id: String,
    val topic: String,
    val questions: List<QuizQuestion>,
    val score: Int,
    val totalQuestions: Int,
    val date: Long = System.currentTimeMillis()
)

// Learning progress model
@Entity(tableName = "learning_progress")
data class LearningProgress(
    @PrimaryKey val topic: String,
    var quizScores: List<Int> = emptyList(), // Store all quiz scores for this topic
    var lastStudied: Long = System.currentTimeMillis(),
    var totalStudyTime: Long = 0 // in minutes
) {
    val averageScore: Double
        get() = if (quizScores.isNotEmpty()) quizScores.average() else 0.0

    val quizzesTaken: Int
        get() = quizScores.size
}

// App settings model
data class AppSettings(
    val isDarkMode: Boolean = false,
    val useVoiceTutoring: Boolean = false,
    val aiModel: String = "GPT-5" // or "Claude"
)