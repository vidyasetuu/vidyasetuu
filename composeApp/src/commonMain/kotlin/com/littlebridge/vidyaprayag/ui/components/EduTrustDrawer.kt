package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.vidyaprayag.ui.theme.AppTheme
import com.littlebridge.vidyaprayag.ui.theme.LocalAppTheme
// EduTrustGradientButton lives in this same package, no import required.
import com.littlebridge.vidyaprayag.ui.theme.LocalThemeSwitcher
import com.littlebridge.vidyaprayag.ui.theme.Motion
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens
import com.littlebridge.vidyaprayag.ui.theme.VidyaGradients

@Composable
fun EduTrustDrawerSheet() {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(300.dp)
    ) {
        DrawerHeader()
        DrawerContent()
        Spacer(modifier = Modifier.weight(1f))
        DrawerFooter()
    }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VidyaGradients.brandSubtle)
            .padding(28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(VidyaGradients.brand),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Guest User",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Welcome back!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        EduTrustGradientButton(
            text = "Get Started",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DrawerContent() {
    val currentTheme = LocalAppTheme.current
    val themeSwitcher = LocalThemeSwitcher.current

    Column(modifier = Modifier.padding(20.dp)) {
        DrawerItem(Icons.Default.Home, "Home", isSelected = true)
        DrawerItem(Icons.Default.Search, "Discovery")
        DrawerItem(Icons.Default.Dashboard, "Management")
        DrawerItem(Icons.Default.Hub, "Tech Ecosystem")

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 14.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        Text(
            "THEME",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        ThemeToggleItem("Day Mode", Icons.Default.LightMode, isSelected = currentTheme == AppTheme.LIGHT) {
            themeSwitcher(AppTheme.LIGHT)
        }
        ThemeToggleItem("Night Mode", Icons.Default.DarkMode, isSelected = currentTheme == AppTheme.DARK) {
            themeSwitcher(AppTheme.DARK)
        }
        ThemeToggleItem("Midnight", Icons.Default.AutoAwesome, isSelected = currentTheme == AppTheme.MIDNIGHT) {
            themeSwitcher(AppTheme.MIDNIGHT)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 14.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        DrawerItem(Icons.Default.Login, "Sign In")
        DrawerItem(Icons.Default.Help, "Support")
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, label: String, isSelected: Boolean = false) {
    val backgroundColor by animateColorTo(
        if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    )
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant

    val indicator by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = Motion.springQuick(),
        label = "drawer-indicator"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VidyaCornerTokens.field)
            .background(backgroundColor, VidyaCornerTokens.field)
            .pressScale(onClick = {})
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) {
            Icon(icon, contentDescription = null, tint = contentColor)
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(Motion.tweenIos()) + slideInHorizontally { -it },
                exit = fadeOut(Motion.tweenIos()) + slideOutHorizontally { -it }
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 3.dp, height = 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            label,
            color = contentColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
        Spacer(Modifier.weight(1f))
        if (indicator > 0f) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = indicator))
            )
        }
    }
}

@Composable
private fun ThemeToggleItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val bg by animateColorTo(
        if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        else Color.Transparent
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg, RoundedCornerShape(14.dp))
            .pressScale(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun DrawerFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "VIDYASETU V0.1",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulsingDot()
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "LIVE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun PulsingDot() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-alpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = alpha))
    )
}

/* tiny inline color animation helper */
@Composable
private fun animateColorTo(target: Color): androidx.compose.runtime.State<Color> =
    androidx.compose.animation.animateColorAsState(
        targetValue = target,
        animationSpec = Motion.tweenEmphasized(),
        label = "drawer-color"
    )


