package com.example.eduaituitor.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eduaituitor.data.LearningProgress
import com.example.eduaituitor.ui.theme.GradientEnd
import com.example.eduaituitor.ui.theme.GradientStart
import com.example.eduaituitor.ui.theme.ProgressExcellent
import com.example.eduaituitor.ui.theme.ProgressGood
import com.example.eduaituitor.ui.theme.ProgressAverage
import com.example.eduaituitor.ui.theme.ProgressNeedsWork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDetailScreen(
    progress: LearningProgress,
    onBack: () -> Unit,
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
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = null,
                            tint = GradientStart
                        )
                        Text(
                            text = progress.topic.replaceFirstChar { it.uppercase() },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Overall Statistics Card
                OverallStatisticsCard(progress = progress)

                // Performance Chart
                PerformanceChartCard(progress = progress)

                // Score Breakdown
                ScoreBreakdownCard(progress = progress)

                // Quiz History
                QuizHistoryCard(progress = progress)

                // Insights and Recommendations
                InsightsCard(progress = progress)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OverallStatisticsCard(progress: LearningProgress) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GradientStart, GradientEnd)
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GradientStart.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Overall Performance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticBox(
                    label = "Average Score",
                    value = "%.1f%%".format(progress.averageScore),
                    icon = "📊"
                )
                StatisticBox(
                    label = "Quizzes Taken",
                    value = "${progress.quizzesTaken}",
                    icon = "📝"
                )
                StatisticBox(
                    label = "Best Score",
                    value = "${progress.quizScores.maxOrNull() ?: 0}%",
                    icon = "🏆"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticBox(
                    label = "Lowest Score",
                    value = "${progress.quizScores.minOrNull() ?: 0}%",
                    icon = "📉"
                )
                StatisticBox(
                    label = "Improvement",
                    value = calculateImprovement(progress),
                    icon = "📈"
                )
                StatisticBox(
                    label = "Consistency",
                    value = getConsistencyLevel(progress),
                    icon = "⭐"
                )
            }
        }
    }
}

@Composable
private fun StatisticBox(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PerformanceChartCard(progress: LearningProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Score Progression",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chart bars
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                progress.quizScores.forEachIndexed { index, score ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Quiz ${index + 1}",
                            modifier = Modifier.width(60.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp)
                                .background(
                                    color = when {
                                        score >= 80 -> ProgressExcellent
                                        score >= 60 -> ProgressGood
                                        score >= 40 -> ProgressAverage
                                        else -> ProgressNeedsWork
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth(fraction = score / 100f)
                        )

                        Text(
                            text = "$score%",
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBreakdownCard(progress: LearningProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Performance Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val excellent = progress.quizScores.count { it >= 80 }
            val good = progress.quizScores.count { it >= 60 && it < 80 }
            val average = progress.quizScores.count { it >= 40 && it < 60 }
            val needsWork = progress.quizScores.count { it < 40 }

            ScoreCategory("Excellent (80-100%)", excellent, ProgressExcellent)
            ScoreCategory("Good (60-79%)", good, ProgressGood)
            ScoreCategory("Average (40-59%)", average, ProgressAverage)
            ScoreCategory("Needs Work (<40%)", needsWork, ProgressNeedsWork)
        }
    }
}

@Composable
private fun ScoreCategory(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$count quiz${if (count != 1) "zes" else ""}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuizHistoryCard(progress: LearningProgress) {
    if (progress.quizScores.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Quiz History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                progress.quizScores.reversed().forEachIndexed { index, score ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            score >= 80 -> ProgressExcellent.copy(alpha = 0.2f)
                            score >= 60 -> ProgressGood.copy(alpha = 0.2f)
                            score >= 40 -> ProgressAverage.copy(alpha = 0.2f)
                            else -> ProgressNeedsWork.copy(alpha = 0.2f)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Attempt ${progress.quizScores.size - index}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$score%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    score >= 80 -> ProgressExcellent
                                    score >= 60 -> ProgressGood
                                    score >= 40 -> ProgressAverage
                                    else -> ProgressNeedsWork
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsCard(progress: LearningProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GradientStart.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "💡 Insights & Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val insights = generateInsights(progress)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                insights.forEach { insight ->
                    Text(
                        text = "• $insight",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Helper functions

private fun calculateImprovement(progress: LearningProgress): String {
    if (progress.quizScores.size < 2) return "N/A"
    val first = progress.quizScores.first()
    val last = progress.quizScores.last()
    val improvement = last - first
    return when {
        improvement > 0 -> "+$improvement%"
        improvement < 0 -> "$improvement%"
        else -> "Stable"
    }
}

private fun getConsistencyLevel(progress: LearningProgress): String {
    if (progress.quizScores.size < 2) return "N/A"
    val average = progress.averageScore
    val variance = progress.quizScores.map { (it - average) * (it - average) }.average()
    val stdDev = Math.sqrt(variance)
    return when {
        stdDev < 5 -> "Very High"
        stdDev < 10 -> "High"
        stdDev < 15 -> "Medium"
        else -> "Low"
    }
}

private fun generateInsights(progress: LearningProgress): List<String> {
    val insights = mutableListOf<String>()
    val average = progress.averageScore

    when {
        average >= 90 -> insights.add("You're mastering this topic! Keep practicing to maintain excellence.")
        average >= 80 -> insights.add("Great progress! You have a strong understanding of this topic.")
        average >= 70 -> insights.add("Good job! You're on the right track. Focus on challenging areas.")
        average >= 60 -> insights.add("You're making progress. Consider reviewing the material once more.")
        else -> insights.add("This topic needs more practice. Try reviewing the key concepts.")
    }

    if (progress.quizScores.size > 1) {
        val improvement = progress.quizScores.last() - progress.quizScores.first()
        if (improvement > 10) {
            insights.add("Your recent quizzes show significant improvement!")
        } else if (improvement < -10) {
            insights.add("Consider revisiting recent lessons if performance is declining.")
        }
    }

    if (progress.quizScores.size >= 5) {
        insights.add("You've taken many quizzes on this topic. Try new topics to broaden your knowledge.")
    }

    insights.add("Last studied: ${getRelativeTime(progress.lastStudied)}")

    return insights
}

@Preview(showBackground = true)
@Composable
fun ProgressDetailScreenPreview() {
    val sampleProgress = LearningProgress(
        topic = "Generative AI",
        quizScores = emptyList(),
        lastStudied = System.currentTimeMillis()
    )

    MaterialTheme {
        ProgressDetailScreen(
            progress = sampleProgress,
            onBack = {}
        )
    }
}
