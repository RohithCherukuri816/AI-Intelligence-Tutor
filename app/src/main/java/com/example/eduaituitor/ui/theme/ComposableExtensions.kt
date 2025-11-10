package com.example.eduaituitor.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Glass morphism effect with blur and semi-transparent background
 */
@Composable
fun Modifier.glassmorphism(
    alpha: Float = 0.1f,
    cornerRadius: Int = 24,
    isDark: Boolean = false
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius.dp))
    .background(
        color = if (isDark) Color.Black.copy(alpha = alpha) else Color.White.copy(alpha = alpha)
    )
    .blur(2.dp)

/**
 * Shimmering loading animation effect
 */
@Composable
fun Modifier.shimmerEffect(isDark: Boolean = false): Modifier {
    val shimmerColors = if (isDark) {
        listOf(
            Color.Black.copy(alpha = 0.8f),
            Color.Black.copy(alpha = 0.4f),
            Color.Black.copy(alpha = 0.8f)
        )
    } else {
        listOf(
            Color.White.copy(alpha = 0.8f),
            Color.White.copy(alpha = 0.4f),
            Color.White.copy(alpha = 0.8f)
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 500, 0f),
            end = Offset(translateAnim, 0f)
        )
    )
}

/**
 * Animated gradient background that smoothly transitions between colors
 */
@Composable
fun Modifier.animatedGradientBackground(
    colors: List<Color> = listOf(GradientStart, GradientMiddle, GradientEnd),
    angle: Float = 45f
): Modifier {
    val transition = rememberInfiniteTransition(label = "gradient")
    val animatedAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_alpha"
    )

    return this.background(
        brush = Brush.linearGradient(
            colors = colors + colors[0],
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    )
}

/**
 * Breathing animation modifier (scale up and down infinitely)
 */
@Composable
fun Modifier.breathingAnimation(
    scaleStart: Float = 0.95f,
    scaleEnd: Float = 1.05f,
    durationMillis: Int = 1500
): Modifier {
    val transition = rememberInfiniteTransition(label = "breathing")
    val scale by transition.animateFloat(
        initialValue = scaleStart,
        targetValue = scaleEnd,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    return this.then(Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    })
}

/**
 * Pulse animation modifier (alpha fade in and out)
 */
@Composable
fun Modifier.pulseAnimation(
    durationMillis: Int = 1000
): Modifier {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    return this.graphicsLayer {
        this.alpha = alpha
    }
}

/**
 * Create a gradient brush with primary theme colors
 */
@Composable
fun primaryGradient(): Brush {
    return Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

/**
 * Create a gradient brush with secondary theme colors
 */
@Composable
fun secondaryGradient(): Brush {
    return Brush.linearGradient(
        colors = listOf(GradientMiddle, GradientEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

/**
 * Elevation shadow effect
 */
fun Modifier.elevatedShadow(elevation: Int = 8): Modifier = this
    .then(Modifier.graphicsLayer {
        shadowElevation = elevation.toFloat() * 4
    })
