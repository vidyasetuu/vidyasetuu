package com.littlebridge.vidyaprayag.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape tokens. Big, soft corners follow the NowInAndroid /
 * Material 3 expressive style. Use these everywhere instead of
 * inline RoundedCornerShape values.
 */
val VidyaShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

object VidyaCornerTokens {
    val pill = RoundedCornerShape(percent = 50)
    val sheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val card = RoundedCornerShape(28.dp)
    val cardLarge = RoundedCornerShape(36.dp)
    val hero = RoundedCornerShape(40.dp)
    val tile = RoundedCornerShape(20.dp)
    val chip = RoundedCornerShape(14.dp)
    val field = RoundedCornerShape(16.dp)
}
