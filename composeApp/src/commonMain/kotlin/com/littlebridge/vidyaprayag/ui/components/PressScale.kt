package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import com.littlebridge.vidyaprayag.ui.theme.Motion
import kotlinx.coroutines.launch

/**
 * iOS-style press-scale modifier — when the user touches the surface
 * it gently shrinks to [pressedScale] using a critically damped spring,
 * exactly like a UIButton on iOS.
 *
 * If [onClick] is provided this is also a complete clickable surface;
 * otherwise it can be combined with another clickable / Button.
 */
@Composable
fun Modifier.pressScale(
    enabled: Boolean = true,
    pressedScale: Float = 0.96f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && pressed) pressedScale else 1f,
        animationSpec = Motion.springQuick(),
        label = "press-scale"
    )

    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val gesture = if (onClick != null) {
        Modifier.pointerInput(enabled, onClick) {
            detectTapGestures(
                onPress = { offset ->
                    if (!enabled) return@detectTapGestures
                    val press = PressInteraction.Press(offset)
                    scope.launch { interactionSource.emit(press) }
                    val released = tryAwaitRelease()
                    scope.launch {
                        interactionSource.emit(
                            if (released) PressInteraction.Release(press)
                            else PressInteraction.Cancel(press)
                        )
                    }
                },
                onTap = { if (enabled) onClick() }
            )
        }
    } else Modifier

    this.scale(scale).then(gesture)
}
