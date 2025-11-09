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
import com.example.eduaituitor.ui.screens.ProgressScreen
import com.example.eduaituitor.ui.screens.QuizScreen
import com.example.eduaituitor.ui.screens.SettingsScreen
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
                    EduAIApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun EduAIApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    
    when (currentScreen) {
        is MainViewModel.Screen.Welcome -> {
            val appSettings by viewModel.appSettings.collectAsState()
            WelcomeScreen(
                onStartLearning = { viewModel.navigateTo(MainViewModel.Screen.Chat) },
                isDarkMode = appSettings.isDarkMode,
                onToggleDarkMode = { viewModel.toggleDarkMode() }
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
                onNavigateToSettings = { viewModel.navigateTo(MainViewModel.Screen.Settings) },
                onBackToWelcome = { viewModel.navigateTo(MainViewModel.Screen.Welcome) },
                onNewChat = { viewModel.startNewChat() },
                onShowHistory = { /* future implementation */ }
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
                onResetProgress = { viewModel.resetProgress() }
            )
        }
        is MainViewModel.Screen.Settings -> {
            val appSettings by viewModel.appSettings.collectAsState()

            SettingsScreen(
                settings = appSettings,
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onToggleVoiceTutoring = { viewModel.toggleVoiceTutoring() },
                onClearChatHistory = { viewModel.clearChatHistory() },
                onBack = { viewModel.navigateTo(MainViewModel.Screen.Chat) }
            )
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