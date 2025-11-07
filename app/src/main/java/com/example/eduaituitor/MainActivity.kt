package com.example.eduaituitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.eduaituitor.ui.screens.ChatScreen
import com.example.eduaituitor.ui.screens.ProgressScreen
import com.example.eduaituitor.ui.screens.QuizScreen
import com.example.eduaituitor.ui.screens.WelcomeScreen
import com.example.eduaituitor.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val appSettings by viewModel.appSettings.collectAsState()
            
            EduAIAppTheme(darkTheme = appSettings.isDarkMode) {
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
            WelcomeScreen(
                onStartLearning = { viewModel.navigateTo(MainViewModel.Screen.Chat) }
            )
        }
        is MainViewModel.Screen.Chat -> {
            ChatScreen(
                messages = viewModel.chatMessages.value,
                isLoading = viewModel.isLoading.value,
                onSendMessage = { message -> viewModel.sendMessage(message) },
                onGenerateQuiz = { viewModel.generateQuiz() },
                onNavigateToProgress = { viewModel.navigateTo(MainViewModel.Screen.Progress) }
            )
        }
        is MainViewModel.Screen.Quiz -> {
            QuizScreen(
                questions = viewModel.quizQuestions.value,
                currentQuestionIndex = 0,
                isLoading = viewModel.isLoading.value,
                onAnswerSelected = { questionIndex, answerIndex ->
                    viewModel.answerQuestion(questionIndex, answerIndex)
                },
                onSubmitQuiz = { viewModel.submitQuizAnswers() },
                onBackToChat = { viewModel.navigateTo(MainViewModel.Screen.Chat) }
            )
        }
        is MainViewModel.Screen.Progress -> {
            ProgressScreen(
                progressList = viewModel.learningProgress.value,
                onBack = { viewModel.navigateTo(MainViewModel.Screen.Chat) },
                onResetProgress = { viewModel.resetProgress() }
            )
        }
        is MainViewModel.Screen.Settings -> {
            // For now, redirect to Progress screen
            ProgressScreen(
                progressList = viewModel.learningProgress.value,
                onBack = { viewModel.navigateTo(MainViewModel.Screen.Chat) },
                onResetProgress = { viewModel.resetProgress() }
            )
        }
    }
}

@Composable
fun EduAIAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}