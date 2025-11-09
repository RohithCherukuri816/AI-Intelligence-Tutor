package com.example.eduaituitor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.eduaituitor.data.AppSettings
import com.example.eduaituitor.data.ChatMessage
import com.example.eduaituitor.data.LearningProgress
import com.example.eduaituitor.data.QuizQuestion
import com.example.eduaituitor.data.QuizSession
import com.example.eduaituitor.data.database.AppDatabase
import com.example.eduaituitor.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Room Database
    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "eduai_database"
    ).build()

    private val progressDao = database.learningProgressDao()
    private val quizSessionDao = database.quizSessionDao()

    private val aiRepository = AIRepository(application)

    // UI States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _currentTopic = MutableStateFlow("")
    val currentTopic: StateFlow<String> = _currentTopic.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _learningProgress = MutableStateFlow<List<LearningProgress>>(emptyList())
    val learningProgress: StateFlow<List<LearningProgress>> = _learningProgress.asStateFlow()

    // Navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    sealed class Screen {
        object Welcome : Screen()
        object Chat : Screen()
        object Quiz : Screen()
        object Progress : Screen()
        object Settings : Screen()
    }

    init {
        // Load progress from database
        loadProgressFromDatabase()
    }

    private fun loadProgressFromDatabase() {
        viewModelScope.launch {
            progressDao.getAllProgress().collect { progressList ->
                _learningProgress.value = progressList
            }
        }
    }

    // Chat functionality
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            // Add user message
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = message,
                isUser = true
            )
            _chatMessages.value = _chatMessages.value + userMessage

            // Update current topic
            if (_currentTopic.value.isEmpty()) {
                _currentTopic.value = extractTopic(message)
            }

            // Get AI response
            try {
                val aiResponse = aiRepository.getAIResponse(message, _chatMessages.value)
                val aiMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = aiResponse,
                    isUser = false,
                    topic = _currentTopic.value
                )
                _chatMessages.value = _chatMessages.value + aiMessage
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "I'm sorry, I encountered an error: ${e.message}. Please try again.",
                    isUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Quiz functionality
    fun generateQuiz() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val questions = aiRepository.generateQuiz(_currentTopic.value)
                _quizQuestions.value = questions
                _currentQuestionIndex.value = 0
                _currentScreen.value = Screen.Quiz
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "Failed to generate quiz: ${e.message}",
                    isUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _quizQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitQuizAnswers() {
        viewModelScope.launch {
            val score = _quizQuestions.value.count { it.userAnswer == it.correctAnswer }
            val total = _quizQuestions.value.size

            // Save quiz session to database
            val quizSession = QuizSession(
                id = UUID.randomUUID().toString(),
                topic = _currentTopic.value,
                questions = _quizQuestions.value,
                score = score,
                totalQuestions = total
            )
            quizSessionDao.insertSession(quizSession)

            // Update learning progress in database
            updateLearningProgress(quizSession)

            // Navigate back to chat
            _currentScreen.value = Screen.Chat

            // Add quiz result message
            val resultMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Great job on the quiz! You scored $score out of $total (${(score * 100 / total)}%). ${
                    getMotivationalMessage(
                        score,
                        total
                    )
                }",
                isUser = false
            )
            _chatMessages.value = _chatMessages.value + resultMessage
        }
    }

    fun answerQuestion(questionIndex: Int, answerIndex: Int) {
        val updatedQuestions = _quizQuestions.value.toMutableList()
        if (questionIndex in updatedQuestions.indices) {
            updatedQuestions[questionIndex] =
                updatedQuestions[questionIndex].copy(userAnswer = answerIndex)
            _quizQuestions.value = updatedQuestions
        }
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Settings
    fun toggleDarkMode() {
        _appSettings.value = _appSettings.value.copy(isDarkMode = !_appSettings.value.isDarkMode)
    }

    fun toggleVoiceTutoring() {
        _appSettings.value = _appSettings.value.copy(useVoiceTutoring = !_appSettings.value.useVoiceTutoring)
    }

    fun clearChatHistory() {
        _chatMessages.value = emptyList()
        _currentTopic.value = ""
    }

    fun startNewChat() {
        clearChatHistory()
    }

    // Progress tracking
    private suspend fun updateLearningProgress(quizSession: QuizSession) {
        val existingProgress = progressDao.getAllProgress()
        var found = false

        existingProgress.collect { progressList ->
            val existing = progressList.find { it.topic == quizSession.topic }
            if (existing != null) {
                // Update existing progress
                val updatedProgress = existing.copy(
                    quizScores = existing.quizScores + quizSession.score,
                    lastStudied = System.currentTimeMillis()
                )
                progressDao.updateProgress(updatedProgress)
                found = true
            }

            if (!found) {
                // Insert new progress
                val newProgress = LearningProgress(
                    topic = quizSession.topic,
                    quizScores = listOf(quizSession.score),
                    lastStudied = System.currentTimeMillis()
                )
                progressDao.insertProgress(newProgress)
            }

            // Exit collection after first emission
            return@collect
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            // Clear database
            _learningProgress.value.forEach { progress ->
                progressDao.updateProgress(progress.copy(quizScores = emptyList()))
            }
            _learningProgress.value = emptyList()
            _chatMessages.value = emptyList()
            _currentTopic.value = ""
        }
    }

    // Utility functions
    private fun extractTopic(message: String): String {
        // Simple topic extraction - in real app, use AI to detect topic
        val words = message.lowercase().split(" ")
        val topicKeywords =
            listOf("teach", "explain", "learn", "about", "what", "is", "me", "the", "a", "an")
        val topic = words.find { !topicKeywords.contains(it) && it.length > 3 }
        return topic?.replaceFirstChar { it.uppercase() } ?: "General Knowledge"
    }

    private fun getMotivationalMessage(score: Int, total: Int): String {
        val percentage = score.toDouble() / total.toDouble()
        return when {
            percentage >= 0.8 -> "Excellent work! You've mastered this topic! "
            percentage >= 0.6 -> "Good job! You're making great progress. Keep it up! "
            else -> "Keep practicing! You'll get better with more study. Don't give up! "
        }
    }

    override fun onCleared() {
        super.onCleared()
        database.close()
    }
}

/**
 * Factory for creating MainViewModel with Application parameter
 */
class MainViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}