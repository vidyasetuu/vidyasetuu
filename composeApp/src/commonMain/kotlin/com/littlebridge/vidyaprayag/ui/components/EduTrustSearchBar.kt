package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.vidyaprayag.ui.theme.Motion
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens
import com.littlebridge.vidyaprayag.ui.theme.VidyaGradients

/**
 * Premium discovery search bar — two stacked pill rows (location + board)
 * plus a gradient search button that springs on press.
 */
@Composable
fun EduTrustSearchBar(
    modifier: Modifier = Modifier,
    locationText: String = "Enter City / Region",
    boardText: String = "CBSE Board",
    onSearchClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onBoardClick: () -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    EduTrustCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface,
        elevation = 12,
        shape = VidyaCornerTokens.card
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Location row
            SearchRow(
                icon = Icons.Default.LocationOn,
                label = locationText,
                onClick = onLocationClick,
                emphasised = false
            )

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BoardPill(
                    text = boardText,
                    onClick = onBoardClick,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                FilterButton(onClick = onFilterClick)
                Spacer(Modifier.width(8.dp))
                GradientSearchButton(onClick = onSearchClick)
            }
        }
    }
}

@Composable
private fun SearchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    emphasised: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = Motion.springQuick(),
        label = "search-row"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(VidyaCornerTokens.field)
            .background(
                if (emphasised) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                VidyaCornerTokens.field
            )
            .pressScale(
                interactionSource = interactionSource,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun BoardPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(VidyaCornerTokens.field)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                VidyaCornerTokens.field
            )
            .pressScale(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FilterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(VidyaCornerTokens.field)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                VidyaCornerTokens.field
            )
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Tune,
            contentDescription = "Filters",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GradientSearchButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(VidyaCornerTokens.field)
            .background(VidyaGradients.brand, VidyaCornerTokens.field)
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
    }
}
