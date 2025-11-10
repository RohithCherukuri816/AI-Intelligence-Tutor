package com.example.eduaituitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eduaituitor.ui.screens.ChatScreen
import com.example.eduaituitor.ui.screens.ChatHistoryScreen
import com.example.eduaituitor.ui.screens.ProgressScreen
import com.example.eduaituitor.ui.screens.ProgressDetailScreen
import com.example.eduaituitor.ui.screens.QuizScreen
import com.example.eduaituitor.ui.screens.QuizHistoryScreen
import com.example.eduaituitor.ui.screens.ResultsScreen
import com.example.eduaituitor.ui.screens.WelcomeScreen
import com.example.eduaituitor.ui.theme.EduAITuitorTheme
import com.example.eduaituitor.viewmodel.MainViewModel
import com.example.eduaituitor.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(application)
            )
            val appSettings by viewModel.appSettings.collectAsState()

            EduAITuitorTheme(darkTheme = appSettings.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EduAIApp(viewModel, appSettings.isDarkMode, { viewModel.toggleDarkMode() })
                }
            }
        }
    }
}

@Composable
fun EduAIApp(viewModel: MainViewModel, isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    
    when (currentScreen) {
        is MainViewModel.Screen.Welcome -> {
            WelcomeScreen(
                onStartLearning = { viewModel.navigateTo(MainViewModel.Screen.Chat) },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
        is MainViewModel.Screen.Chat -> {
            val messages by viewModel.chatMessages.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            ChatScreen(
                messages = messages,
                isLoading = isLoading,
                onSendMessage = { message -> viewModel.sendMessage(message) },
                onGenerateQuiz = { viewModel.generateQuiz() },
                onNavigateToProgress = { viewModel.navigateTo(MainViewModel.Screen.Progress) },
                onBackToWelcome = { viewModel.navigateTo(MainViewModel.Screen.Welcome) },
                onNewChat = { viewModel.startNewChat() },
                onShowHistory = { viewModel.navigateTo(MainViewModel.Screen.ChatHistory) },
                onEditMessage = { messageId, newText -> viewModel.editMessage(messageId, newText) }
            )
        }
        is MainViewModel.Screen.ChatHistory -> {
            val allMessages by viewModel.chatMessages.collectAsState()
            val quizSessions by viewModel.quizSessions.collectAsState()

            ChatHistoryScreen(
                allMessages = allMessages,
                allQuizSessions = quizSessions,
                onBack = { viewModel.navigateTo(MainViewModel.Screen.Chat) },
                onTopicClick = { topic, messages -> viewModel.openTopicChat(topic, messages) },
                onDeleteTopic = { topic -> viewModel.deleteTopicMessages(topic) },
                onDeleteQuiz = { quizSession -> viewModel.deleteQuiz(quizSession) }
            )
        }

        is MainViewModel.Screen.ChatDetail -> {
            val messages by viewModel.chatMessages.collectAsState()

            ChatScreen(
                messages = messages,
                isLoading = false,
                onSendMessage = { message -> viewModel.sendMessage(message) },
                onGenerateQuiz = { viewModel.generateQuiz() },
                onNavigateToProgress = { viewModel.navigateTo(MainViewModel.Screen.Progress) },
                onBackToWelcome = { viewModel.navigateTo(MainViewModel.Screen.ChatHistory) },
                onNewChat = { viewModel.startNewChat() },
                onShowHistory = { viewModel.navigateTo(MainViewModel.Screen.ChatHistory) },
                onEditMessage = { messageId, newText -> viewModel.editMessage(messageId, newText) }
            )
        }

        is MainViewModel.Screen.QuizHistory -> {
            val quizSessions by viewModel.quizSessions.collectAsState()

            QuizHistoryScreen(
                quizSessions = quizSessions,
                onBack = { viewModel.navigateTo(MainViewModel.Screen.Chat) }
            )
        }
        is MainViewModel.Screen.Quiz -> {
            val questions by viewModel.quizQuestions.collectAsState()
            val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            QuizScreen(
                questions = questions,
                currentQuestionIndex = currentQuestionIndex,
                isLoading = isLoading,
                onAnswerSelected = { questionIndex, answerIndex ->
                    viewModel.answerQuestion(questionIndex, answerIndex)
                },
                onNextQuestion = { viewModel.nextQuestion() },
                onPreviousQuestion = { viewModel.previousQuestion() },
                onSubmitQuiz = { viewModel.submitQuizAnswers() },
                onBackToChat = { viewModel.navigateTo(MainViewModel.Screen.Chat) }
            )
        }
        is MainViewModel.Screen.Progress -> {
            val progressList by viewModel.learningProgress.collectAsState()

            ProgressScreen(
                progressList = progressList,
                onBack = { viewModel.navigateTo(MainViewModel.Screen.Chat) },
                onResetProgress = { viewModel.resetProgress() },
                onProgressClick = { progress -> viewModel.viewProgressDetail(progress) }
            )
        }
        is MainViewModel.Screen.Results -> {
            val quizScore by viewModel.quizScore.collectAsState()
            val totalQuestions by viewModel.totalQuestions.collectAsState()
            val currentTopic by viewModel.currentTopic.collectAsState()

            ResultsScreen(
                score = quizScore,
                totalQuestions = totalQuestions,
                topic = currentTopic,
                onBackToChat = { viewModel.navigateTo(MainViewModel.Screen.Chat) }
            )
        }
        is MainViewModel.Screen.ProgressDetail -> {
            val selectedProgress by viewModel.selectedProgress.collectAsState()

            if (selectedProgress != null) {
                ProgressDetailScreen(
                    progress = selectedProgress!!,
                    onBack = { viewModel.navigateTo(MainViewModel.Screen.Progress) }
                )
            }
        }
    }
}

@Composable
fun EduAITuitorTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}