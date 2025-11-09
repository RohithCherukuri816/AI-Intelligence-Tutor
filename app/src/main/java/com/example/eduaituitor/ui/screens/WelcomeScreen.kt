package com.example.eduaituitor.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eduaituitor.ui.components.*
import com.example.eduaituitor.ui.theme.*

@Composable
fun WelcomeScreen(
    onStartLearning: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Animated gradient background
        AnimatedGradientBackground(
            modifier = Modifier.fillMaxSize(),
            colors = listOf(
                GradientStart.copy(alpha = 0.3f),
                GradientMiddle.copy(alpha = 0.2f),
                GradientEnd.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            )
        }

        // Floating particles
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            particleCount = 15
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Hero Section
            FadeInContent(delayMillis = 200) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Animated icon with glow effect
                    PulsingIcon(
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .shadow(
                                    elevation = 24.dp,
                                    shape = CircleShape,
                                    ambientColor = MaterialTheme.colorScheme.primary,
                                    spotColor = MaterialTheme.colorScheme.primary
                                )
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            GradientStart,
                                            GradientMiddle,
                                            GradientEnd
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "EduAI Tutor",
                                modifier = Modifier.size(64.dp),
                                tint = Color.White
                            )
                        }
                    }

                    // App title with gradient
                    Text(
                        text = "EduAI Tutor",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 48.sp,
                            brush = Brush.linearGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle
                    Text(
                        text = "Your AI-Powered",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Learning Companion",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Feature cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FeatureCard(
                            icon = Icons.Default.Chat,
                            title = "AI Chat",
                            delay = 400
                        )
                        FeatureCard(
                            icon = Icons.Default.Quiz,
                            title = "Quizzes",
                            delay = 500
                        )
                        FeatureCard(
                            icon = Icons.Default.TrendingUp,
                            title = "Progress",
                            delay = 600
                        )
                    }
                }
            }

            // Bottom section
            FadeInContent(delayMillis = 800) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Privacy badge
                    Surface(
                        modifier = Modifier
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Privacy",
                                tint = SuccessLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "100% Privacy • On-Device AI",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Theme toggle card
                    ThemeToggleCard(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = onToggleDarkMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    // CTA Button with gradient and animation
                    BouncingIcon(enabled = true) {
                        Button(
                            onClick = onStartLearning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(32.dp),
                                    ambientColor = MaterialTheme.colorScheme.primary,
                                    spotColor = MaterialTheme.colorScheme.primary
                                ),
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                GradientStart,
                                                GradientMiddle,
                                                GradientEnd
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Start Learning Journey",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom text
                    Text(
                        text = "Join thousands of learners worldwide",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    delay: Int
) {
    FadeInContent(delayMillis = delay) {
        Surface(
            modifier = Modifier
                .size(100.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ThemeToggleCard(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isDarkMode) GradientStart else GradientEnd
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isDarkMode) "Dark Mode" else "Light Mode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tap to switch appearance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AssistChip(
                onClick = onToggleDarkMode,
                label = {
                    Text(if (isDarkMode) "Light" else "Dark")
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    EduAITuitorTheme {
        WelcomeScreen(
            onStartLearning = {},
            isDarkMode = false,
            onToggleDarkMode = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WelcomeScreenDarkPreview() {
    EduAITuitorTheme(darkTheme = true) {
        WelcomeScreen(
            onStartLearning = {},
            isDarkMode = true,
            onToggleDarkMode = {}
        )
    }
}