package com.example.eduaituitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eduaituitor.data.QuizSession
import com.example.eduaituitor.ui.theme.GradientEnd
import com.example.eduaituitor.ui.theme.GradientMiddle
import com.example.eduaituitor.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizHistoryScreen(
    quizSessions: List<QuizSession>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedSessions = quizSessions.sortedByDescending { it.date }

    // Calculate statistics
    val totalQuizzes = sortedSessions.size
    val averageScore = if (sortedSessions.isNotEmpty()) {
        sortedSessions.map { (it.score.toDouble() / it.totalQuestions) * 100 }.average()
    } else 0.0
    val bestScore = sortedSessions.maxOfOrNull { (it.score.toDouble() / it.totalQuestions) * 100 } ?: 0.0
    val worstScore = sortedSessions.minOfOrNull { (it.score.toDouble() / it.totalQuestions) * 100 } ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assessment,
                            contentDescription = null,
                            tint = GradientStart
                        )
                        Text(
                            text = "Quiz History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (sortedSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assessment,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "No quizzes taken yet",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Generate and take a quiz to track your progress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Overall statistics card
                    item {
                        QuizStatisticsCard(
                            totalQuizzes = totalQuizzes,
                            averageScore = averageScore,
                            bestScore = bestScore,
                            worstScore = worstScore
                        )
                    }

                    // Quiz sessions
                    item {
                        Text(
                            text = "Quiz Sessions (${totalQuizzes})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(sortedSessions) { session ->
                        QuizSessionCard(session = session, index = sortedSessions.indexOf(session) + 1)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizStatisticsCard(
    totalQuizzes: Int,
    averageScore: Double,
    bestScore: Double,
    worstScore: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, GradientStart.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.1f),
                            GradientMiddle.copy(alpha = 0.05f),
                            GradientEnd.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        label = "Total Quizzes",
                        value = totalQuizzes.toString(),
                        icon = Icons.Filled.Assessment
                    )
                    StatItem(
                        label = "Average Score",
                        value = String.format("%.1f%%", averageScore),
                        icon = Icons.Filled.TrendingUp
                    )
                }

                // Bottom row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        label = "Best Score",
                        value = String.format("%.1f%%", bestScore),
                        icon = Icons.Filled.EmojiEvents
                    )
                    StatItem(
                        label = "Lowest Score",
                        value = String.format("%.1f%%", worstScore),
                        icon = Icons.Filled.TrendingDown
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GradientStart,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GradientStart
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuizSessionCard(session: QuizSession, index: Int) {
    val scorePercentage = (session.score.toDouble() / session.totalQuestions) * 100
    val backgroundColor = when {
        scorePercentage >= 80 -> GradientStart.copy(alpha = 0.1f)
        scorePercentage >= 60 -> GradientMiddle.copy(alpha = 0.1f)
        else -> GradientEnd.copy(alpha = 0.1f)
    }
    val borderColor = when {
        scorePercentage >= 80 -> GradientStart
        scorePercentage >= 60 -> GradientMiddle
        else -> GradientEnd
    }

    val emoji = when {
        scorePercentage >= 90 -> "🏆"
        scorePercentage >= 80 -> "🎯"
        scorePercentage >= 70 -> "👏"
        scorePercentage >= 60 -> "📚"
        else -> "💡"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Attempt number and topic
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Attempt #$index",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = session.topic,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatDate(session.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right: Score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "${session.score}/${session.totalQuestions}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = borderColor
                )
                Text(
                    text = String.format("%.1f%%", scorePercentage),
                    style = MaterialTheme.typography.labelSmall,
                    color = borderColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val today = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
    val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    return when {
        messageDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                messageDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }
        messageDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 &&
                messageDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
            "Yesterday at " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        }
        messageDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
        else -> {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizHistoryScreenPreview() {
    MaterialTheme {
        QuizHistoryScreen(
            quizSessions = emptyList(),
            onBack = {}
        )
    }
}