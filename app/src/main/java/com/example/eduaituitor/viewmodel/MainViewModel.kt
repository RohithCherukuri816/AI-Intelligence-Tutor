package com.example.eduaituitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduaituitor.data.AppSettings
import com.example.eduaituitor.data.ChatMessage
import com.example.eduaituitor.data.LearningProgress
import com.example.eduaituitor.data.QuizQuestion
import com.example.eduaituitor.data.QuizSession
import com.example.eduaituitor.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    private val aiRepository = AIRepository()

    // UI States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _currentTopic = MutableStateFlow("")
    val currentTopic: StateFlow<String> = _currentTopic.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

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
                    content = "I'm sorry, I encountered an error. Please try again.",
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
                _currentScreen.value = Screen.Quiz
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitQuizAnswers() {
        viewModelScope.launch {
            val score = _quizQuestions.value.count { it.userAnswer == it.correctAnswer }
            val total = _quizQuestions.value.size

            // Save quiz session
            val quizSession = QuizSession(
                id = UUID.randomUUID().toString(),
                topic = _currentTopic.value,
                questions = _quizQuestions.value,
                score = score,
                totalQuestions = total
            )

            // Update learning progress
            updateLearningProgress(quizSession)

            // Navigate back to chat
            _currentScreen.value = Screen.Chat

            // Add quiz result message
            val resultMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Great job on the quiz! You scored $score out of $total. ${getMotivationalMessage(score, total)}",
                isUser = false
            )
            _chatMessages.value = _chatMessages.value + resultMessage
        }
    }

    fun answerQuestion(questionIndex: Int, answerIndex: Int) {
        val updatedQuestions = _quizQuestions.value.toMutableList()
        updatedQuestions[questionIndex] = updatedQuestions[questionIndex].copy(userAnswer = answerIndex)
        _quizQuestions.value = updatedQuestions
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

    // Progress tracking
    private fun updateLearningProgress(quizSession: QuizSession) {
        val currentProgress = _learningProgress.value.toMutableList()
        val existingProgress = currentProgress.find { it.topic == quizSession.topic }

        if (existingProgress != null) {
            existingProgress.quizScores = existingProgress.quizScores + quizSession.score
            existingProgress.lastStudied = System.currentTimeMillis()
        } else {
            val newProgress = LearningProgress(
                topic = quizSession.topic,
                quizScores = listOf(quizSession.score)
            )
            currentProgress.add(newProgress)
        }

        _learningProgress.value = currentProgress
    }

    fun resetProgress() {
        _learningProgress.value = emptyList()
        _chatMessages.value = emptyList()
        _currentTopic.value = ""
    }

    // Utility functions
    private fun extractTopic(message: String): String {
        // Simple topic extraction - in real app, use AI to detect topic
        val words = message.split(" ").map { it.toLowerCase() }
        val topicKeywords = listOf("teach", "explain", "learn", "about", "what is")
        val topic = words.find { !topicKeywords.contains(it) && it.length > 3 }
        return topic ?: "general knowledge"
    }

    private fun getMotivationalMessage(score: Int, total: Int): String {
        val percentage = score.toDouble() / total.toDouble()
        return when {
            percentage >= 0.8 -> "Excellent work! You've mastered this topic!"
            percentage >= 0.6 -> "Good job! You're making great progress."
            else -> "Keep practicing! You'll get better with more study."
        }
    }
}