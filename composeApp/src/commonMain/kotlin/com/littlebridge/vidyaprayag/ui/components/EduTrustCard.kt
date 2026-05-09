package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens

/**
 * Premium card. Defaults match the NowInAndroid tonal card — soft
 * elevation shadow, subtle outline, rounded large corners, and an
 * optional press-scale interaction when [onClick] is provided.
 */
@Composable
fun EduTrustCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    backgroundBrush: Brush? = null,
    elevation: Int = 6,
    border: BorderStroke? = null,
    shape: Shape = VidyaCornerTokens.cardLarge,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val effectiveBorder = border ?: BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f)
    )
    val interactionSource = remember { MutableInteractionSource() }

    val container: Modifier = modifier
        .shadow(
            elevation = elevation.dp,
            shape = shape,
            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                .compositeOver(Color.Black),
            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                .compositeOver(Color.Black)
        )
        .clip(shape)
        .let { base ->
            if (backgroundBrush != null) {
                base.background(backgroundBrush, shape)
            } else {
                base.background(backgroundColor, shape)
            }
        }
        .border(effectiveBorder, shape)
        .let { base ->
            if (onClick != null) base.pressScale(
                interactionSource = interactionSource,
                onClick = onClick
            ) else base
        }

    Box(modifier = container) {
        content()
    }
}

/** Tonal "section" surface used for soft inline groupings (no shadow). */
@Composable
fun EduTrustTonalSurface(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        .compositeOver(MaterialTheme.colorScheme.surface),
    shape: Shape = VidyaCornerTokens.tile,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = shape,
        tonalElevation = 0.dp
    ) {
        content()
    }
}

@Composable
fun EduTrustChip(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
    }
}
