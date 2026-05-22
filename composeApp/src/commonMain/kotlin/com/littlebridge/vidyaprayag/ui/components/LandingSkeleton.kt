package com.littlebridge.vidyaprayag.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Skeleton screen for the landing page. Mirrors the real layout
 * silhouette so the swap from skeleton → content feels seamless.
 */
@Composable
fun LandingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Hero headline
        ShimmerLine(height = 36.dp, widthFraction = 0.85f, cornerRadius = 12.dp)
        Spacer(Modifier.height(12.dp))
        ShimmerLine(height = 36.dp, widthFraction = 0.65f, cornerRadius = 12.dp)
        Spacer(Modifier.height(28.dp))

        // Hero image
        ShimmerBox(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(280.dp),
            cornerRadius = 36.dp
        )
        Spacer(Modifier.height(24.dp))

        // Search card
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            cornerRadius = 28.dp
        )
        Spacer(Modifier.height(40.dp))

        // Featured schools row
        ShimmerLine(height = 22.dp, widthFraction = 0.5f, cornerRadius = 10.dp)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(2) {
                ShimmerBox(
                    modifier = Modifier.width(220.dp).height(220.dp),
                    cornerRadius = 28.dp
                )
            }
        }
        Spacer(Modifier.height(40.dp))

        // Entry point cards
        repeat(2) {
            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                cornerRadius = 36.dp
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
