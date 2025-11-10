package com.example.eduaituitor.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eduaituitor.data.ChatMessage
import com.example.eduaituitor.data.QuizSession
import com.example.eduaituitor.ui.theme.GradientEnd
import com.example.eduaituitor.ui.theme.GradientMiddle
import com.example.eduaituitor.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.*

data class TopicChatHistory(
    val topic: String,
    val messages: List<ChatMessage>,
    val messageCount: Int,
    val lastMessage: String,
    val timestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    allMessages: List<ChatMessage>,
    allQuizSessions: List<QuizSession>,
    onBack: () -> Unit,
    onTopicClick: (String, List<ChatMessage>) -> Unit,
    onDeleteTopic: (String) -> Unit,
    onQuizClick: (QuizSession) -> Unit = {},
    onDeleteQuiz: (QuizSession) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Group messages by topic
    val topicGroups = allMessages
        .filter { it.topic.isNotBlank() }  // Only include messages with non-empty topics
        .groupBy { it.topic }
        .map { (topic, messages) ->
            TopicChatHistory(
                topic = topic,
                messages = messages,
                messageCount = messages.size,
                lastMessage = messages.lastOrNull()?.content?.take(60) ?: "No messages",
                timestamp = messages.lastOrNull()?.timestamp ?: 0L
            )
        }
        .sortedByDescending { it.timestamp }

    val quizSessionsSorted = allQuizSessions.sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (selectedTab == 0) {
                            Icon(imageVector = Icons.Filled.History, contentDescription = null, tint = GradientStart)
                            Text(text = "Chat History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Filled.Assessment, contentDescription = null, tint = GradientEnd)
                            Text(text = "Quiz History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
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
        Column(modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            // Tab row
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(imageVector = Icons.Filled.History, contentDescription = "Chat") }, text = { Text("Chat") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(imageVector = Icons.Filled.Assessment, contentDescription = "Quiz") }, text = { Text("Quiz") })
            }

            if (selectedTab == 0) {
                ChatHistoryContent(topicGroups, onTopicClick, onDeleteTopic)
            } else {
                QuizHistoryContent(quizSessionsSorted, onQuizClick, onDeleteQuiz)
            }
        }
    }
}

@Composable
private fun ChatHistoryContent(
    topicGroups: List<TopicChatHistory>,
    onTopicClick: (String, List<ChatMessage>) -> Unit,
    onDeleteTopic: (String) -> Unit
) {
    if (topicGroups.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(imageVector = Icons.Filled.History, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                Text(text = "No chat history yet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "Start a conversation to see your chat history here", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(text = "You have ${topicGroups.size} topic${if (topicGroups.size != 1) "s" else ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(topicGroups) { topicHistory ->
                ChatTopicCard(topicHistory = topicHistory, onClick = { onTopicClick(topicHistory.topic, topicHistory.messages) }, onDelete = { onDeleteTopic(topicHistory.topic) })
            }
        }
    }
}

@Composable
private fun QuizHistoryContent(
    quizSessionsSorted: List<QuizSession>,
    onQuizClick: (QuizSession) -> Unit,
    onDeleteQuiz: (QuizSession) -> Unit
) {
    if (quizSessionsSorted.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(imageVector = Icons.Filled.Assessment, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                Text(text = "No quiz history yet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "Take a quiz to see your history here", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(text = "You have ${quizSessionsSorted.size} quiz${if (quizSessionsSorted.size != 1) "zes" else ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(quizSessionsSorted) { quizSession ->
                QuizHistoryCard(quizSession = quizSession, onClick = { onQuizClick(quizSession) }, onDelete = { onDeleteQuiz(quizSession) })
            }
        }
    }
}

@Composable
private fun ChatTopicCard(topicHistory: TopicChatHistory, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }, shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = topicHistory.topic, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = topicHistory.lastMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${topicHistory.messageCount} message${if (topicHistory.messageCount != 1) "s" else ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = getChatHistoryRelativeTime(topicHistory.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete chat", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Delete Chat?") }, text = { Text("Are you sure you want to delete all messages from \"${topicHistory.topic}\"? This action cannot be undone.") }, confirmButton = { TextButton(onClick = { onDelete(); showDeleteDialog = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } })
    }
}

@Composable
private fun QuizHistoryCard(quizSession: QuizSession, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scorePercentage = (quizSession.score.toDouble() / quizSession.totalQuestions) * 100

    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }, shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text(text = quizSession.topic.ifBlank { "General Knowledge" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = if (scorePercentage >= 50) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown, contentDescription = null, tint = if (scorePercentage >= 50) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    Text(text = "Score: ${quizSession.score}/${quizSession.totalQuestions}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    if (scorePercentage == 100.0) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = "Perfect!", tint = GradientEnd)
                        Text("Perfect!", style = MaterialTheme.typography.labelMedium, color = GradientEnd)
                    }
                }
                Text(text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(quizSession.date)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete quiz", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Delete Quiz?") }, text = { Text("Are you sure you want to delete this quiz session? This action cannot be undone.") }, confirmButton = { TextButton(onClick = { onDelete(); showDeleteDialog = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } })
    }
}

fun getChatHistoryRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        diff < 604800000 -> "${diff / 86400000}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatHistoryScreenPreview() {
    val sampleMessages = listOf(
        ChatMessage(id = "1", content = "Explain photosynthesis", isUser = true, topic = "Photosynthesis", timestamp = System.currentTimeMillis() - 60000),
        ChatMessage(id = "2", content = "Photosynthesis is...", isUser = false, topic = "Photosynthesis", timestamp = System.currentTimeMillis() - 50000)
    )

    MaterialTheme {
        ChatHistoryScreen(
            allMessages = sampleMessages,
            allQuizSessions = emptyList(),
            onBack = {},
            onTopicClick = { _, _ -> },
            onDeleteTopic = {},
            onQuizClick = {},
            onDeleteQuiz = {}
        )
    }
}