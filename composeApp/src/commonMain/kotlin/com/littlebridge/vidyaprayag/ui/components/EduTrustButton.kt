package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.vidyaprayag.ui.theme.Motion
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens
import com.littlebridge.vidyaprayag.ui.theme.VidyaGradients

/**
 * Premium button system. Buttons are NOT wrapped in [Button] — they are
 * built from a [Box] with a custom [pressScale] gesture so we get
 * iOS-style spring shrink on press, plus first-class support for:
 *  • gradient backgrounds
 *  • leading icons
 *  • smooth loading state via AnimatedContent
 */

private val ButtonHeight = 56.dp
private val ButtonShape = VidyaCornerTokens.field

@Composable
fun EduTrustPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    EduTrustButtonImpl(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
        background = SolidColor(MaterialTheme.colorScheme.primary),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        border = null
    )
}

@Composable
fun EduTrustGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    EduTrustButtonImpl(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
        background = VidyaGradients.brand,
        contentColor = Color.White,
        border = null
    )
}

@Composable
fun EduTrustSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    EduTrustButtonImpl(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
        background = SolidColor(MaterialTheme.colorScheme.secondary),
        contentColor = MaterialTheme.colorScheme.onSecondary,
        border = null
    )
}

@Composable
fun EduTrustOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    EduTrustButtonImpl(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
        background = SolidColor(Color.Transparent),
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    )
}

@Composable
private fun EduTrustButtonImpl(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    leadingIcon: ImageVector?,
    background: Brush,
    contentColor: Color,
    border: BorderStroke?
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isInteractive = enabled && !loading

    val containerAlpha = if (enabled) 1f else 0.45f
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .height(ButtonHeight)
            .clip(ButtonShape)
            .pressScale(
                enabled = isInteractive,
                interactionSource = interactionSource,
                onClick = { if (isInteractive) onClick() }
            )
            .background(background, ButtonShape)
            .let { if (!enabled) it.background(onSurface.copy(alpha = 0.08f), ButtonShape) else it }
            .let { base ->
                if (border != null) {
                    base.then(Modifier.border(border, ButtonShape))
                } else base
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor.copy(alpha = containerAlpha)) {
                AnimatedContent(
                    targetState = loading,
                    transitionSpec = {
                        (fadeIn(tween(Motion.Duration.Short, easing = Motion.Standard)) +
                            scaleIn(initialScale = 0.85f, animationSpec = Motion.springQuick()))
                            .togetherWith(
                                fadeOut(tween(Motion.Duration.Short, easing = Motion.Standard)) +
                                    scaleOut(targetScale = 0.85f, animationSpec = Motion.springQuick())
                            )
                    },
                    label = "btn-state"
                ) { isLoading ->
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = contentColor,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (leadingIcon != null) {
                                Icon(
                                    leadingIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = contentColor.copy(alpha = containerAlpha)
                                )
                                Spacer(Modifier.width(10.dp))
                            }
                            Text(
                                text,
                                style = MaterialTheme.typography.labelLarge,
                                color = contentColor.copy(alpha = containerAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Compact, icon-only round button used in top bars. */
@Composable
fun EduTrustIconChip(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    background: Color = MaterialTheme.colorScheme.surfaceVariant
        .copy(alpha = 0.5f).compositeOver(MaterialTheme.colorScheme.surface),
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(shape)
            .background(background, shape)
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(20.dp))
    }
}
