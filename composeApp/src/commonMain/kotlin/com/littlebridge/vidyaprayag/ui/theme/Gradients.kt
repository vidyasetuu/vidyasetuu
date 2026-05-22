package com.littlebridge.vidyaprayag.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

/**
 * Premium gradient palette — used for hero sections, CTA surfaces,
 * portal cards, and the marquee. Centralising these means brand
 * tweaks ripple through the whole app instantly.
 */
object VidyaGradients {

    val brand: Brush
        @Composable @ReadOnlyComposable get() = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.secondary
            )
        )

    val brandSubtle: Brush
        @Composable @ReadOnlyComposable get() = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
            )
        )

    val hero: Brush
        @Composable @ReadOnlyComposable get() = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )

    val emerald: Brush
        @Composable @ReadOnlyComposable get() = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.secondaryContainer
            )
        )

    val surfaceVeil: Brush
        @Composable @ReadOnlyComposable get() = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.background.copy(alpha = 0.65f),
                MaterialTheme.colorScheme.background
            )
        )

    val ctaOverlay: Brush
        @Composable @ReadOnlyComposable get() = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
            )
        )

    val shimmer: Brush
        @Composable @ReadOnlyComposable get() = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            )
        )
}
