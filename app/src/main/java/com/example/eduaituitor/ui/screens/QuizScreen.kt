package com.example.eduaituitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eduaituitor.data.QuizQuestion
import com.example.eduaituitor.ui.theme.GradientEnd
import com.example.eduaituitor.ui.theme.GradientStart
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.scale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    questions: List<QuizQuestion>,
    currentQuestionIndex: Int,
    isLoading: Boolean,
    onAnswerSelected: (Int, Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    onSubmitQuiz: () -> Unit,
    onBackToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = GradientStart
                        )
                        Text(
                            text = "Quiz Challenge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToChat) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Generating quiz questions...")
                    }
                }
            } else if (questions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No quiz questions available",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Progress indicator
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = GradientStart,
                        trackColor = GradientEnd.copy(alpha = 0.2f),
                        progress = (currentQuestionIndex + 1).toFloat() / questions.size
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = questions[currentQuestionIndex].question,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Options
                            questions[currentQuestionIndex].options.forEachIndexed { index, option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (questions[currentQuestionIndex].userAnswer == index) {
                                                GradientStart.copy(alpha = 0.12f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            }
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = questions[currentQuestionIndex].userAnswer == index,
                                        onClick = { onAnswerSelected(currentQuestionIndex, index) }
                                    )
                                    Text(
                                        text = option,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .weight(1f),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onPreviousQuestion,
                            enabled = currentQuestionIndex > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GradientEnd.copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Previous")
                        }

                        if (currentQuestionIndex < questions.size - 1) {
                            Button(
                                onClick = onNextQuestion,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GradientStart
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Next")
                            }
                        } else {
                            Button(
                                onClick = onSubmitQuiz,
                                enabled = questions.all { it.userAnswer != -1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GradientStart
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Submit Quiz")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    val sampleQuestions = listOf(
        QuizQuestion(
            "What is the primary energy source for photosynthesis?",
            listOf("Water", "Sunlight", "Oxygen", "Carbon dioxide"),
            1
        )
    )

    MaterialTheme {
        QuizScreen(
            questions = sampleQuestions,
            currentQuestionIndex = 0,
            isLoading = false,
            onAnswerSelected = { _, _ -> },
            onNextQuestion = {},
            onPreviousQuestion = {},
            onSubmitQuiz = {},
            onBackToChat = {}
        )
    }
}

// ============ RESULTS SCREEN ============

@Composable
fun ResultsScreen(
    score: Int,
    totalQuestions: Int,
    topic: String,
    onBackToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = (score * 100 / totalQuestions)
    val animatedScore = androidx.compose.animation.core.animateIntAsState(
        targetValue = score,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500)
    )
    val animatedPercentage = androidx.compose.animation.core.animateIntAsState(
        targetValue = percentage,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500)
    )
    val scaleAnimation = androidx.compose.animation.core.animateFloatAsState(
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = 0.5f,
            stiffness = 200f
        )
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            // Celebration emoji/icon
            Text(
                text = if (percentage >= 80) "🎉" else if (percentage >= 60) "🌟" else "💪",
                fontSize = 80.sp,
                modifier = Modifier.scale(scaleAnimation.value)
            )

            // Score Circle with animation
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .scale(scaleAnimation.value),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${animatedPercentage.value}%",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 60.sp
                        ),
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = "${animatedScore.value}/${totalQuestions}",
                        style = MaterialTheme.typography.titleLarge,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Topic Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GradientStart.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Topic",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Feedback message
            val feedbackMessage = when {
                percentage >= 90 -> "Outstanding! Perfect mastery! 🏆"
                percentage >= 80 -> "Excellent work! You've mastered this topic! 🎯"
                percentage >= 70 -> "Great job! You're doing well! 👏"
                percentage >= 60 -> "Good effort! Keep practicing to improve! 📚"
                percentage >= 50 -> "Not bad! Review the material and try again! 💡"
                else -> "Keep learning! This will get easier with practice! 🚀"
            }

            Text(
                text = feedbackMessage,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                color = when {
                    percentage >= 80 -> GradientStart
                    percentage >= 60 -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                    else -> androidx.compose.material3.MaterialTheme.colorScheme.secondary
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBackToChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GradientStart
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Back to Chat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenPreview() {
    MaterialTheme {
        ResultsScreen(
            score = 8,
            totalQuestions = 10,
            topic = "Photosynthesis",
            onBackToChat = {}
        )
    }
}