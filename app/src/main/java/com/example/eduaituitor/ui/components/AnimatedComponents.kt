package com.example.eduaituitor.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.eduaituitor.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated gradient background for premium look
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(GradientStart, GradientMiddle, GradientEnd),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    Box(
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(offset * 1000f, offset * 1000f),
                end = Offset((1 - offset) * 1000f, (1 - offset) * 1000f)
            )
        )
    ) {
        content()
    }
}

/**
 * Typing indicator animation for AI responses
 */
@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = EaseInOutCubic, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp * scale)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

/**
 * Shimmer effect for loading states
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    if (isLoading) {
        Box(
            modifier = modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ShimmerLight.copy(alpha = 0.3f),
                            ShimmerLight.copy(alpha = 0.8f),
                            ShimmerLight.copy(alpha = 0.3f)
                        ),
                        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                        end = Offset(translateAnim, translateAnim)
                    )
                )
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

/**
 * Animated counter for scores
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineLarge
) {
    var currentCount by remember { mutableStateOf(0) }

    LaunchedEffect(count) {
        val step = if (count > currentCount) 1 else -1
        while (currentCount != count) {
            currentCount += step
            delay(30)
        }
    }

    Text(
        text = currentCount.toString(),
        style = style,
        modifier = modifier
    )
}

/**
 * Pulsing heart animation for streaks
 */
@Composable
fun PulsingIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier.scale(scale),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Wave animation for background
 */
@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    waveColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveAnimation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val waveHeight = size.height * 0.1f
        val wavelength = size.width / 2f

        val path = Path().apply {
            moveTo(0f, size.height / 2)

            for (x in 0..size.width.toInt() step 10) {
                val y =
                    size.height / 2 + waveHeight * sin((x + animatedValue) * (2 * Math.PI / wavelength)).toFloat()
                lineTo(x.toFloat(), y)
            }

            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(
            path = path,
            color = waveColor
        )
    }
}

/**
 * Floating particles animation
 */
@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    particleCount: Int = 20
) {
    val particles = remember {
        List(particleCount) {
            ParticleState(
                x = Random.nextInt(0, 1001).toFloat(),
                y = Random.nextInt(0, 1001).toFloat(),
                size = Random.nextInt(2, 9).toFloat(),
                speedX = Random.nextFloat() * 2f - 1f,
                speedY = Random.nextFloat() * -1.5f - 0.5f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particleColor,
                radius = particle.size,
                center = Offset(
                    particle.x % size.width,
                    particle.y % size.height
                )
            )

            particle.x += particle.speedX
            particle.y += particle.speedY

            if (particle.y < 0) particle.y = size.height
            if (particle.x < 0) particle.x = size.width
            if (particle.x > size.width) particle.x = 0f
        }
    }
}

data class ParticleState(
    var x: Float,
    var y: Float,
    val size: Float,
    val speedX: Float,
    val speedY: Float
)

/**
 * Animated progress bar with gradient
 */
@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    colors: List<Color> = listOf(GradientStart, GradientMiddle, GradientEnd),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progressAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(
                    brush = Brush.horizontalGradient(colors)
                )
                .clip(RoundedCornerShape(height / 2))
        )
    }
}

/**
 * Bouncing icon animation
 */
@Composable
fun BouncingIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (enabled) -20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    Box(
        modifier = modifier.offset(y = offsetY.dp)
    ) {
        content()
    }
}

/**
 * Fade in animation for content
 */
@Composable
fun FadeInContent(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(800, easing = EaseOutCubic)
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Scale animation for buttons
 */
@Composable
fun Modifier.scaleOnPress(pressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scalePress"
    )
    return this.scale(scale)
}

@Composable
fun Modifier.scale(scale: Float): Modifier {
    return this.then(
        Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    )
}

// Extension function for smooth value animations
@Composable
fun animateIntAsState(
    targetValue: Int,
    label: String = "intAnimation"
): State<Int> {
    val animatedFloat by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(800, easing = EaseOutCubic),
        label = label
    )
    return remember { derivedStateOf { animatedFloat.toInt() } }
}