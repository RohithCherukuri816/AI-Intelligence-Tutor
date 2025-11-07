package com.example.eduaituitor.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.eduaituitor.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("chat")
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Quiz") },
                    label = { Text("Quiz") },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("quiz")
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Progress") },
                    label = { Text("Progress") },
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                        navController.navigate("progress")
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedItem == 3,
                    onClick = {
                        selectedItem = 3
                        navController.navigate("settings")
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("chat") { 
                ChatScreen(
                    messages = emptyList(),
                    isLoading = false,
                    onSendMessage = {},
                    onGenerateQuiz = { navController.navigate("quiz") },
                    onNavigateToProgress = { navController.navigate("progress") }
                )
            }
            composable("quiz") { 
                QuizScreen(
                    questions = emptyList(),
                    currentQuestionIndex = 0,
                    isLoading = false,
                    onAnswerSelected = { _, _ -> },
                    onSubmitQuiz = {},
                    onBackToChat = { navController.navigate("chat") }
                )
            }
            composable("progress") { 
                ProgressScreen(
                    progressList = emptyList(),
                    onBack = { navController.navigate("chat") },
                    onResetProgress = {}
                )
            }
            composable("settings") { 
                SettingsScreen() 
            }
        }
    }
}
