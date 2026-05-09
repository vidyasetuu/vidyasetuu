package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * iOS-grade shimmer modifier. Uses an infinite linear transition
 * that translates a soft highlight gradient across the surface.
 * Pair with [ShimmerBox] / [ShimmerLine] / [ShimmerCircle] to build
 * skeleton placeholders.
 */
@Composable
fun Modifier.shimmer(
    enabled: Boolean = true,
    durationMillis: Int = 1400,
    base: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        .compositeOver(MaterialTheme.colorScheme.surface),
    highlight: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        .compositeOver(MaterialTheme.colorScheme.surface)
): Modifier {
    if (!enabled) return this

    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-progress"
    )

    val widthPx = 1000f
    val translate = (progress * 2f - 1f) * widthPx

    val brush = Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(translate, 0f),
        end = Offset(translate + widthPx, 0f)
    )

    return this.then(Modifier.background(brush))
}

@Composable
fun ShimmerLine(
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
    cornerRadius: Dp = 8.dp,
    widthFraction: Float = 1f
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .shimmer()
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .shimmer()
    )
}

@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    diameter: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(diameter)
            .clip(RoundedCornerShape(diameter))
            .shimmer()
    )
}
