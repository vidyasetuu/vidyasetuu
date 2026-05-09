package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.vidyaprayag.ui.theme.Motion
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens

/**
 * Premium floating top bar. The back/menu button cross-fades when the
 * route changes, the avatar shows a subtle pulse on changes, and the
 * whole surface uses a frosted tonal background that subtly lifts as
 * the user scrolls (controlled via [elevatedFraction]).
 */
@Composable
fun EduTrustTopBar(
    onMenuClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    title: String = "EduTrust",
    elevatedFraction: Float = 0f
) {
    val containerAlpha by animateFloatAsState(
        targetValue = 0.55f + (elevatedFraction.coerceIn(0f, 1f) * 0.4f),
        animationSpec = Motion.tweenEmphasized(),
        label = "topbar-alpha"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = 0.06f + (elevatedFraction.coerceIn(0f, 1f) * 0.18f),
        animationSpec = Motion.tweenEmphasized(),
        label = "topbar-border"
    )
    val verticalPad by animateDpAsState(
        targetValue = if (elevatedFraction > 0.1f) 10.dp else 14.dp,
        animationSpec = Motion.tweenEmphasized(),
        label = "topbar-pad"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(VidyaCornerTokens.field)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = containerAlpha))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = borderAlpha),
                shape = VidyaCornerTokens.field
            )
            .padding(horizontal = 8.dp, vertical = verticalPad)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = onBackClick != null,
                    transitionSpec = {
                        (fadeIn(Motion.tweenIos(Motion.Duration.Short)) +
                            scaleIn(initialScale = 0.7f, animationSpec = Motion.springQuick()))
                            .togetherWith(
                                fadeOut(Motion.tweenIos(Motion.Duration.Quick)) +
                                    scaleOut(targetScale = 0.7f, animationSpec = Motion.springQuick())
                            )
                    },
                    label = "topbar-leading"
                ) { showBack ->
                    if (showBack) {
                        EduTrustIconChip(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = { onBackClick?.invoke() },
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        EduTrustIconChip(
                            icon = Icons.Default.Menu,
                            onClick = onMenuClick,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.6).sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                EduTrustIconChip(
                    icon = Icons.Default.Notifications,
                    onClick = {},
                    contentDescription = "Notifications",
                    background = Color.Transparent,
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.18f).compositeOver(Color.Transparent),
                            RoundedCornerShape(14.dp)
                        )
                        .pressScale(onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Optional decorative scroll-aware bottom hairline.
        AnimatedVisibility(
            visible = elevatedFraction > 0.5f,
            enter = fadeIn(Motion.tweenEmphasized()),
            exit = fadeOut(Motion.tweenEmphasized())
        ) {
            Spacer(Modifier)
        }
    }
}
