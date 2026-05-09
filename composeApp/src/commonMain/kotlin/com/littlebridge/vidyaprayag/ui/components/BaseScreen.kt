package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.littlebridge.vidyaprayag.ui.theme.Motion
import kotlinx.coroutines.launch

/**
 * App-wide screen scaffold. Provides the modal navigation drawer, the
 * floating top bar, and a [TopBarElevationController] handle so screens
 * can drive the top bar's elevated appearance from their scroll state.
 */
@Composable
fun BaseScreen(
    onBackClick: (() -> Unit)? = null,
    title: String = "EduTrust",
    elevatedFraction: Float = 0f,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val animatedElevation by animateFloatAsState(
        targetValue = elevatedFraction.coerceIn(0f, 1f),
        animationSpec = Motion.tweenEmphasized(),
        label = "topbar-elevation"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { EduTrustDrawerSheet() }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                EduTrustTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onBackClick = onBackClick,
                    title = title,
                    elevatedFraction = animatedElevation
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(paddingValues)
            }
        }
    }
}
