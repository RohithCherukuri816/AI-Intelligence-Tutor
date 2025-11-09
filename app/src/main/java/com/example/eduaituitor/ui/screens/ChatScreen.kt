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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eduaituitor.data.ChatMessage
import com.example.eduaituitor.ui.components.TypingIndicator
import com.example.eduaituitor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onGenerateQuiz: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onBackToWelcome: () -> Unit = {},
    onNewChat: () -> Unit = {},
    onShowHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showHistoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            // Premium Top Bar with Gradient
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                            )
                        )
                ) {
                    Column {
                        // Top Bar Content
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Back button
                            IconButton(
                                onClick = onBackToWelcome,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back to Welcome",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Center: Title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.School,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "EduAI Tutor",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Right: Action buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // New Chat button
                                IconButton(
                                    onClick = {
                                        onNewChat()
                                        messageText = ""
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "New Chat",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // History button
                                IconButton(
                                    onClick = {
                                        showHistoryDialog = true
                                        onShowHistory()
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = "Chat History",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Progress button
                                IconButton(
                                    onClick = onNavigateToProgress,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TrendingUp,
                                        contentDescription = "Progress",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Settings button
                                IconButton(
                                    onClick = onNavigateToSettings,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Status indicator if loading
                        if (isLoading) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White.copy(alpha = 0.8f),
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Welcome message if no messages
                    if (messages.isEmpty()) {
                        item {
                            WelcomeMessage()
                        }
                    }

                    items(messages) { message ->
                        EnhancedChatBubble(message = message)
                    }

                    // Typing indicator
                    if (isLoading && messages.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shadowElevation = 2.dp
                                ) {
                                    TypingIndicator(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Quiz suggestion card
                if (messages.isNotEmpty() && !isLoading && messages.last().isUser == false) {
                    QuizSuggestionCard(
                        onGenerateQuiz = onGenerateQuiz,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Input area
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Text input
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    "Ask me anything...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (messageText.isNotBlank() && !isLoading) {
                                        onSendMessage(messageText)
                                        messageText = ""
                                        keyboardController?.hide()
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GradientStart,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            maxLines = 4
                        )

                        // Send button
                        FloatingActionButton(
                            onClick = {
                                if (messageText.isNotBlank() && !isLoading) {
                                    onSendMessage(messageText)
                                    messageText = ""
                                    keyboardController?.hide()
                                }
                            },
                            containerColor = if (messageText.isNotBlank() && !isLoading) {
                                GradientStart
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Send message",
                                tint = if (messageText.isNotBlank() && !isLoading) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showHistoryDialog) {
        ChatHistoryDialog(
            messages = messages,
            onDismiss = { showHistoryDialog = false }
        )
    }
}

@Composable
private fun WelcomeMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "Hello! I'm your AI Tutor 👋",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ask me anything and I'll help you learn!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    text = "📚 Explain photosynthesis"
                )
                SuggestionChip(
                    text = "⚛️ What is quantum mechanics?"
                )
                SuggestionChip(
                    text = "🔢 Help me with algebra"
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EnhancedChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Bottom),
                shape = CircleShape,
                color = GradientStart
            ) {
                Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            color = if (isUser) GradientStart else MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User Avatar
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Bottom),
                shape = CircleShape,
                color = GradientEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun QuizSuggestionCard(
    onGenerateQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Ready to test yourself?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Take a quiz on this topic!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onGenerateQuiz,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Quiz", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ChatHistoryDialog(
    messages: List<ChatMessage>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Conversation History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (messages.isEmpty()) {
                Text(
                    text = "No messages yet. Start a conversation to see it here!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    messages.forEach { message ->
                        ChatHistoryItem(message = message)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ChatHistoryItem(message: ChatMessage) {
    val label = if (message.isUser) "You" else "Tutor"
    val icon = if (message.isUser) Icons.Filled.Person else Icons.Filled.SmartToy
    val backgroundColor = if (message.isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = if (message.isUser) GradientEnd else GradientStart
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val sampleMessages = listOf(
        ChatMessage(
            id = "1",
            content = "Explain photosynthesis to me",
            isUser = true,
            timestamp = System.currentTimeMillis()
        ),
        ChatMessage(
            id = "2",
            content = "Photosynthesis is the process by which plants convert sunlight into energy...",
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
    )

    MaterialTheme {
        ChatScreen(
            messages = sampleMessages,
            isLoading = false,
            onSendMessage = {},
            onGenerateQuiz = {},
            onNavigateToProgress = {},
            onNavigateToSettings = {},
            onBackToWelcome = {},
            onNewChat = {},
            onShowHistory = {}
        )
    }
}