package com.littlebridge.vidyaprayag.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale — 4dp grid. Always prefer these named tokens
 * over raw .dp values for consistency.
 */
data class Spacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val gutter: Dp = 24.dp,
    val sectionGap: Dp = 40.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
