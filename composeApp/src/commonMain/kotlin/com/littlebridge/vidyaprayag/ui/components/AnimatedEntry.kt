package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.littlebridge.vidyaprayag.ui.theme.Motion
import kotlinx.coroutines.delay

/**
 * Fade + slide-up entrance animation, designed to mimic the
 * staggered reveal pattern Now in Android uses on its For You feed.
 * The first frame after composition triggers the animation in.
 */
@Composable
fun AnimatedEntry(
    modifier: Modifier = Modifier,
    delayMs: Long = 0L,
    initialOffset: Int = 32,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = Motion.Duration.Long,
                easing = Motion.EmphasizedDecelerate
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = Motion.Duration.Long,
                easing = Motion.EmphasizedDecelerate
            ),
            initialOffsetY = { initialOffset * 4 }
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = Motion.Duration.Short,
                easing = Motion.EmphasizedAccelerate
            )
        ) + slideOutVertically(
            animationSpec = tween(
                durationMillis = Motion.Duration.Short,
                easing = Motion.EmphasizedAccelerate
            ),
            targetOffsetY = { initialOffset * 2 }
        )
    ) {
        androidx.compose.foundation.layout.Box(modifier = modifier) {
            content()
        }
    }
}
