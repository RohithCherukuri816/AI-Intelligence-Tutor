package com.example.eduaituitor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.eduaituitor.Constants
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Room Database
    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "eduai_database"
    )
        .fallbackToDestructiveMigration()
        .build()

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

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()

    private val _selectedProgress = MutableStateFlow<LearningProgress?>(null)
    val selectedProgress: StateFlow<LearningProgress?> = _selectedProgress.asStateFlow()

    private val _quizSessions = MutableStateFlow<List<QuizSession>>(emptyList())
    val quizSessions: StateFlow<List<QuizSession>> = _quizSessions.asStateFlow()

    // Navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    sealed class Screen {
        object Welcome : Screen()
        object Chat : Screen()
        object Quiz : Screen()
        object Progress : Screen()
        data class ProgressDetail(val topic: String) : Screen()
        object ChatHistory : Screen()
        data class ChatDetail(val topic: String) : Screen()
        object Results : Screen()
        object QuizHistory : Screen()
    }

    init {
        // Load progress from database
        loadProgressFromDatabase()
        // Load quiz sessions from database
        loadQuizSessionsFromDatabase()
    }

    private fun loadProgressFromDatabase() {
        viewModelScope.launch {
            progressDao.getAllProgress().collect { progressList ->
                _learningProgress.value = progressList
            }
        }
    }

    private fun loadQuizSessionsFromDatabase() {
        viewModelScope.launch {
            quizSessionDao.getAllSessions().collect { sessions ->
                _quizSessions.value = sessions
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
                android.util.Log.d("QuizGeneration", "=== QUIZ GENERATION STARTED ===")
                android.util.Log.d("QuizGeneration", "Current topic: '${_currentTopic.value}'")

                if (_currentTopic.value.isEmpty()) {
                    android.util.Log.w("QuizGeneration", "WARNING: Topic is empty!")
                    val errorMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = "Please ask me about a specific topic first before generating a quiz!",
                        isUser = false
                    )
                    _chatMessages.value = _chatMessages.value + errorMessage
                    return@launch
                }

                android.util.Log.d(
                    "QuizGeneration",
                    "Calling AIRepository.generateQuiz with topic: ${_currentTopic.value}"
                )
                val questions = aiRepository.generateQuiz(_currentTopic.value)

                android.util.Log.d(
                    "QuizGeneration",
                    "Received ${questions.size} questions from AIRepository"
                )

                if (questions.isEmpty()) {
                    android.util.Log.e("QuizGeneration", "ERROR: No questions generated!")
                    val errorMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = "Failed to generate quiz questions. Please try again.",
                        isUser = false
                    )
                    _chatMessages.value = _chatMessages.value + errorMessage
                    return@launch
                }

                questions.forEachIndexed { idx, q ->
                    android.util.Log.d(
                        "QuizGeneration",
                        "Q${idx + 1}: ${q.question.take(60)}... (Correct: ${q.correctAnswer})"
                    )
                }

                _quizQuestions.value = questions
                _currentQuestionIndex.value = 0
                android.util.Log.d("QuizGeneration", "Navigating to Quiz screen")
                _currentScreen.value = Screen.Quiz
                android.util.Log.d("QuizGeneration", "=== QUIZ GENERATION COMPLETED ===")
            } catch (e: Exception) {
                android.util.Log.e("QuizGeneration", "ERROR: ${e.message}", e)
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
            try {
                android.util.Log.d("QuizSubmit", "=== QUIZ SUBMISSION STARTED ===")
                android.util.Log.d(
                    "QuizSubmit",
                    "Total questions in state: ${_quizQuestions.value.size}"
                )

                _quizQuestions.value.forEachIndexed { idx, q ->
                    android.util.Log.d(
                        "QuizSubmit",
                        "Q$idx: userAnswer=${q.userAnswer}, correctAnswer=${q.correctAnswer}"
                    )
                }

                val score = _quizQuestions.value.count { it.userAnswer == it.correctAnswer }
                val total = _quizQuestions.value.size

                android.util.Log.d("QuizSubmit", "Score: $score / $total")

                _quizScore.value = score
                _totalQuestions.value = total

                android.util.Log.d(
                    "QuizSubmit",
                    "Quiz state updated: ${_quizScore.value} / ${_totalQuestions.value}"
                )

                // Save quiz session to database
                val quizSession = QuizSession(
                    id = UUID.randomUUID().toString(),
                    topic = _currentTopic.value,
                    questions = _quizQuestions.value,
                    score = score,
                    totalQuestions = total
                )
                quizSessionDao.insertSession(quizSession)
                android.util.Log.d("QuizSubmit", "Quiz session saved to database")

                // Update learning progress in database
                updateLearningProgress(quizSession)
                android.util.Log.d("QuizSubmit", "Learning progress updated")

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
                android.util.Log.d("QuizSubmit", "Result message added to chat")

                // Navigate to results (do this last)
                android.util.Log.d("QuizSubmit", "Navigating to Results screen")
                android.util.Log.d("QuizSubmit", "Current screen before: ${_currentScreen.value}")
                _currentScreen.value = Screen.Results
                android.util.Log.d("QuizSubmit", "Current screen after: ${_currentScreen.value}")
                android.util.Log.d("QuizSubmit", "=== QUIZ SUBMISSION COMPLETED ===")
            } catch (e: Exception) {
                android.util.Log.e("QuizSubmit", "ERROR: ${e.message}", e)
                e.printStackTrace()
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "Error submitting quiz: ${e.message}",
                    isUser = false
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            }
        }
    }

    fun answerQuestion(questionIndex: Int, answerIndex: Int) {
        android.util.Log.d(
            "QuizAnswer",
            "Question $questionIndex answered with option $answerIndex"
        )

        val updatedQuestions = _quizQuestions.value.toMutableList()
        if (questionIndex in updatedQuestions.indices) {
            updatedQuestions[questionIndex] =
                updatedQuestions[questionIndex].copy(userAnswer = answerIndex)
            _quizQuestions.value = updatedQuestions

            android.util.Log.d(
                "QuizAnswer",
                "Updated question $questionIndex: ${updatedQuestions[questionIndex]}"
            )
            android.util.Log.d(
                "QuizAnswer",
                "All questions answered: ${updatedQuestions.all { it.userAnswer != -1 }}"
            )
        } else {
            android.util.Log.w("QuizAnswer", "Question index $questionIndex out of bounds")
        }
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun viewQuizHistory() {
        _currentScreen.value = Screen.QuizHistory
    }

    fun toggleDarkMode() {
        _appSettings.value = _appSettings.value.copy(isDarkMode = !_appSettings.value.isDarkMode)
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
        try {
            android.util.Log.d(
                "QuizSubmit",
                "Starting updateLearningProgress for topic: ${quizSession.topic}"
            )

            // Get all progress synchronously first
            val allProgress = progressDao.getAllProgress().first()
            android.util.Log.d("QuizSubmit", "Got progress list, size: ${allProgress.size}")

            val existing = allProgress.find { it.topic == quizSession.topic }

            if (existing != null) {
                android.util.Log.d(
                    "QuizSubmit",
                    "Found existing progress for topic: ${quizSession.topic}"
                )
                // Update existing progress
                val updatedProgress = existing.copy(
                    quizScores = existing.quizScores + quizSession.score,
                    lastStudied = System.currentTimeMillis()
                )
                progressDao.updateProgress(updatedProgress)
                android.util.Log.d("QuizSubmit", "Updated existing progress")
            } else {
                android.util.Log.d(
                    "QuizSubmit",
                    "No existing progress, creating new for topic: ${quizSession.topic}"
                )
                // Insert new progress
                val newProgress = LearningProgress(
                    topic = quizSession.topic,
                    quizScores = listOf(quizSession.score),
                    lastStudied = System.currentTimeMillis()
                )
                progressDao.insertProgress(newProgress)
                android.util.Log.d("QuizSubmit", "Inserted new progress")
            }

            android.util.Log.d("QuizSubmit", "updateLearningProgress completed successfully")
        } catch (e: Exception) {
            android.util.Log.e("QuizSubmit", "ERROR in updateLearningProgress: ${e.message}", e)
            e.printStackTrace()
            // Don't throw - let submission continue
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            try {
                // Delete all quiz sessions from database
                _quizSessions.value.forEach { quizSession ->
                    quizSessionDao.deleteSession(quizSession)
                }

                // Delete all learning progress from database
                _learningProgress.value.forEach { progress ->
                    progressDao.deleteProgress(progress)
                }

                // Clear all UI states
                _learningProgress.value = emptyList()
                _quizSessions.value = emptyList()
                _chatMessages.value = emptyList()
                _currentTopic.value = ""

                android.util.Log.d(
                    "ResetProgress",
                    "All progress, quizzes, and chat data completely cleared"
                )
            } catch (e: Exception) {
                android.util.Log.e("ResetProgress", "Error resetting progress: ${e.message}", e)
            }
        }
    }

    fun viewProgressDetail(progress: LearningProgress) {
        _selectedProgress.value = progress
        _currentScreen.value = Screen.ProgressDetail(progress.topic)
    }

    // Chat history management
    fun deleteTopicMessages(topic: String) {
        viewModelScope.launch {
            progressDao.deleteProgressByTopic(topic)
        }
        _chatMessages.value = _chatMessages.value.filter { it.topic != topic }
        _learningProgress.value = _learningProgress.value.filter { it.topic != topic }
        android.util.Log.d("ChatHistory", "Deleted messages for topic: $topic")
    }

    fun deleteQuiz(quizSession: QuizSession) {
        viewModelScope.launch {
            try {
                quizSessionDao.deleteSession(quizSession)
                _quizSessions.value = _quizSessions.value.filter { it.id != quizSession.id }
                android.util.Log.d("QuizHistory", "Deleted quiz session: ${quizSession.id}")
            } catch (e: Exception) {
                android.util.Log.e("QuizHistory", "Error deleting quiz: ${e.message}", e)
            }
        }
    }

    fun openTopicChat(topic: String, messages: List<ChatMessage>) {
        _chatMessages.value = messages
        _currentTopic.value = topic
        _currentScreen.value = Screen.ChatDetail(topic)
    }

    fun viewChatHistory() {
        _currentScreen.value = Screen.ChatHistory
    }

    // Message editing
    fun editMessage(messageId: String, newContent: String) {
        viewModelScope.launch {
            try {
                // Remove the old user message and its following AI response
                val messages = _chatMessages.value.toMutableList()
                val messageIndex = messages.indexOfFirst { it.id == messageId }

                if (messageIndex >= 0) {
                    // Remove user message
                    messages.removeAt(messageIndex)

                    // Remove the AI response that follows (if exists)
                    if (messageIndex < messages.size && !messages[messageIndex].isUser) {
                        messages.removeAt(messageIndex)
                    }

                    _chatMessages.value = messages
                }

                // Now send the edited message as a new message
                sendMessage(newContent)

                android.util.Log.d("ChatEdit", "Message $messageId edited and resent")
            } catch (e: Exception) {
                android.util.Log.e("ChatEdit", "Error editing message: ${e.message}", e)
            }
        }
    }

    // Utility functions
    private fun extractTopic(message: String): String {
        // Simple topic extraction - in real app, use AI to detect topic
        val words = message.lowercase().split(" ")
        val topicKeywords =
            listOf("teach", "explain", "learn", "about", "what", "is", "me", "the", "a", "an")
        val topic = words.find { !topicKeywords.contains(it) && it.length > 3 }
        return topic?.replaceFirstChar { it.uppercase() } ?: ""
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