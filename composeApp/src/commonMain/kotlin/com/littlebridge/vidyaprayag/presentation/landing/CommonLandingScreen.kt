package com.littlebridge.vidyaprayag.presentation.landing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littlebridge.vidyaprayag.domain.util.UiState
import com.littlebridge.vidyaprayag.feature.schools.domain.model.School
import com.littlebridge.vidyaprayag.navigation.Destination
import com.littlebridge.vidyaprayag.navigation.LocalAppNavigator
import com.littlebridge.vidyaprayag.presentation.MainViewModel
import com.littlebridge.vidyaprayag.ui.auth.AuthBottomSheet
import com.littlebridge.vidyaprayag.ui.components.AnimatedEntry
import com.littlebridge.vidyaprayag.ui.components.BaseScreen
import com.littlebridge.vidyaprayag.ui.components.EduTrustCard
import com.littlebridge.vidyaprayag.ui.components.EduTrustChip
import com.littlebridge.vidyaprayag.ui.components.EduTrustGradientButton
import com.littlebridge.vidyaprayag.ui.components.EduTrustOutlinedButton
import com.littlebridge.vidyaprayag.ui.components.EduTrustPrimaryButton
import com.littlebridge.vidyaprayag.ui.components.EduTrustSearchBar
import com.littlebridge.vidyaprayag.ui.components.EduTrustSecondaryButton
import com.littlebridge.vidyaprayag.ui.components.LandingSkeleton
import com.littlebridge.vidyaprayag.ui.theme.Motion
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens
import com.littlebridge.vidyaprayag.ui.theme.VidyaGradients
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommonLandingScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val schoolsState by viewModel.schools.collectAsStateMpp()
    val navigator = LocalAppNavigator.current

    var showAuthSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val elevatedFraction by remember {
        derivedStateOf {
            // Compute a smooth 0..1 fraction over the first ~80px of scroll.
            val first = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            if (first > 0) 1f else (offset / 80f).coerceIn(0f, 1f)
        }
    }

    BaseScreen(
        title = "VidyaSetu",
        elevatedFraction = elevatedFraction
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = schoolsState,
                transitionSpec = {
                    (fadeIn(Motion.tweenEmphasized(Motion.Duration.Long))
                        .togetherWith(fadeOut(Motion.tweenEmphasized(Motion.Duration.Short))))
                },
                label = "landing-state",
                contentKey = { state ->
                    when (state) {
                        is UiState.Loading -> 0
                        is UiState.Error -> 1
                        is UiState.Success -> 2
                    }
                }
            ) { state ->
                when (state) {
                    is UiState.Loading -> LandingSkeleton(
                        modifier = Modifier.padding(paddingValues)
                    )
                    is UiState.Error -> ErrorState(
                        message = state.message,
                        modifier = Modifier.padding(paddingValues),
                        onRetry = { viewModel.refreshSchools() }
                    )
                    is UiState.Success -> LandingContent(
                        schools = state.data,
                        listState = listState,
                        paddingValues = paddingValues,
                        onSearchClick = { navigator.navigateTo(Destination.Search) },
                        onSchoolClick = { id -> navigator.navigateTo(Destination.SchoolDetails(id)) },
                        onJoinClick = { showAuthSheet = true },
                        onLoginClick = { showAuthSheet = true }
                    )
                }
            }

            if (showAuthSheet) {
                AuthBottomSheet(onDismissRequest = { showAuthSheet = false })
            }
        }
    }
}

/* small alias so the call site reads naturally */
@Composable
private fun <T> kotlinx.coroutines.flow.StateFlow<T>.collectAsStateMpp() = collectAsState()

@Composable
private fun LandingContent(
    schools: List<School>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    paddingValues: PaddingValues,
    onSearchClick: () -> Unit,
    onSchoolClick: (String) -> Unit,
    onJoinClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item("hero") {
            AnimatedEntry(delayMs = 0) { HeroSection(onSearchClick = onSearchClick) }
        }
        if (schools.isNotEmpty()) {
            item("featured") {
                AnimatedEntry(delayMs = 80) {
                    FeaturedSchoolsSection(schools = schools, onSchoolClick = onSchoolClick)
                }
            }
        }
        item("social") {
            AnimatedEntry(delayMs = 120) { SocialProofSection() }
        }
        item("entry") {
            AnimatedEntry(delayMs = 160) { EntryPointsSection(onJoinClick = onJoinClick) }
        }
        item("moat") {
            AnimatedEntry(delayMs = 200) { MoatShowcaseSection() }
        }
        item("portal") {
            AnimatedEntry(delayMs = 240) { PortalAccessSection(onLoginClick = onLoginClick) }
        }
        item("cta") {
            AnimatedEntry(delayMs = 280) { FinalCtaSection(onJoinClick = onJoinClick) }
        }
        item("footer") {
            AnimatedEntry(delayMs = 320) { FooterSection() }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        EduTrustCard(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Something went wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                EduTrustPrimaryButton(text = "Try Again", onClick = onRetry, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/* ---------------- Hero ---------------- */

@Composable
private fun HeroSection(onSearchClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EduTrustChip(label = "INDIA'S SCHOOL INTELLIGENCE LAYER")
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Education with Trust.",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Progress with Purpose.",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Hero artwork with soft gradient frame.
        Box(
            modifier = Modifier
                .size(320.dp)
                .clip(VidyaCornerTokens.hero)
                .background(VidyaGradients.brandSubtle, VidyaCornerTokens.hero)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), VidyaCornerTokens.hero),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida/ADBb0ujKS0F1JiULtLeeVDpTqgaNyFbwA67q0g2mU5kpdJ3STuxY-9WZXhkeDtjdHaEErpJQ2WGLrgQBMs8LG5ZxDw45A_TiYvX37WedwysCnF5r2iOJHOitbJg5S0uwgXuTeU1jGHtw6cEuOj-pNLPrdwJh92Cr8i2q0fXbSuAv0HWfUBbUGilE3F8PgjdfdfEe3SesTla-LxmAJWgi6JfGq4p3gTnHdmAkzetEsjjgZGiwHlacf8cCz-NRhWs",
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(VidyaCornerTokens.hero),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        EduTrustSearchBar(onSearchClick = onSearchClick)
    }
}

/* ---------------- Featured ---------------- */

@Composable
private fun FeaturedSchoolsSection(
    schools: List<School>,
    onSchoolClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Featured Institutions",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
            Text(
                "View all",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            "Verified profiles updated weekly",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            schools.forEach { school ->
                SchoolCard(school = school, onClick = { onSchoolClick(school.id) })
            }
        }
    }
}

@Composable
private fun SchoolCard(
    school: School,
    onClick: () -> Unit
) {
    EduTrustCard(
        modifier = Modifier.width(240.dp),
        onClick = onClick,
        elevation = 8
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
            ) {
                AsyncImage(
                    model = school.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    EduTrustChip(label = school.board)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    school.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    school.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoGraph,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "SRI Verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* ---------------- Social Proof ---------------- */

@Composable
private fun SocialProofSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Text(
            "TRUSTED BY 500+ INSTITUTIONS",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MarqueeItem(Icons.Default.AccountBalance, "Academix")
            MarqueeItem(Icons.Default.Token, "GlobalView")
            MarqueeItem(Icons.Default.Layers, "EduPulse")
            MarqueeItem(Icons.Default.Hub, "AMU")
        }
    }
}

@Composable
private fun MarqueeItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.alpha(0.65f)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/* ---------------- Entry Points ---------------- */

@Composable
private fun EntryPointsSection(onJoinClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        EntryPointCard(
            label = "FOR PARENTS",
            title = "Find the perfect school for your child's unique journey",
            description = "Empowering parents with data-driven insights and verified institutional profiles.",
            features = listOf(
                "Verified institutional profiles",
                "Smart Comparison highlights",
                "AI Career Paths & Talent ID"
            ),
            buttonText = "Start Your Search",
            gradient = true,
            onButtonClick = onJoinClick
        )
        Spacer(modifier = Modifier.height(20.dp))
        EntryPointCard(
            label = "FOR SCHOOLS",
            title = "Scale excellence with intelligence.",
            description = "Advanced institutional management tools designed for modern educational growth.",
            features = listOf(
                "Full Admissions CRM",
                "Teacher Accountability",
                "Automated Compliance"
            ),
            buttonText = "Onboard Your School",
            gradient = false,
            onButtonClick = onJoinClick
        )
    }
}

@Composable
private fun EntryPointCard(
    label: String,
    title: String,
    description: String,
    features: List<String>,
    buttonText: String,
    gradient: Boolean,
    onButtonClick: () -> Unit
) {
    EduTrustCard(modifier = Modifier.fillMaxWidth(), elevation = 10) {
        Column(modifier = Modifier.padding(28.dp)) {
            EduTrustChip(label = label)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        feature,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (gradient) {
                EduTrustGradientButton(
                    text = buttonText,
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                EduTrustPrimaryButton(
                    text = buttonText,
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/* ---------------- Moat ---------------- */

@Composable
private fun MoatShowcaseSection() {
    Column(modifier = Modifier.padding(vertical = 32.dp)) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                "Next-Gen Intelligence",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Proprietary systems powering the ecosystem.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoatCard(
                icon = Icons.Default.Chat,
                title = "WhatsApp-First",
                description = "Seamless communication between parents and faculty without app fatigue."
            )
            MoatCard(
                icon = Icons.Default.AutoGraph,
                title = "SRI Index",
                description = "Standardized Reliability Index for objective school performance tracking."
            )
            MoatCard(
                icon = Icons.Default.Layers,
                title = "PEWS Engine",
                description = "Predictive Early Warning System flags risks before they become problems."
            )
        }
    }
}

@Composable
private fun MoatCard(icon: ImageVector, title: String, description: String) {
    EduTrustCard(modifier = Modifier.width(280.dp), elevation = 6) {
        Column(modifier = Modifier.padding(28.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(VidyaGradients.brandSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ---------------- Portals ---------------- */

@Composable
private fun PortalAccessSection(onLoginClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    "Access Your Portal",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Already part of the ecosystem?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "View All",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        PortalCard(Icons.Default.FamilyRestroom, "Parent Portal",
            "Track progress, monitor safety, and connect with faculty.", onLoginClick)
        Spacer(modifier = Modifier.height(12.dp))
        PortalCard(Icons.Default.AdminPanelSettings, "Admin Center",
            "Manage institutional compliance and staff performance.", onLoginClick)
        Spacer(modifier = Modifier.height(12.dp))
        PortalCard(Icons.Default.AssignmentInd, "Teacher App",
            "Update attendance, grades, and student reports daily.", onLoginClick)
    }
}

@Composable
private fun PortalCard(icon: ImageVector, title: String, description: String, onLoginClick: () -> Unit) {
    EduTrustCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            .compositeOver(MaterialTheme.colorScheme.surface),
        elevation = 0,
        onClick = onLoginClick
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            EduTrustOutlinedButton(
                text = "Log In",
                onClick = onLoginClick,
                modifier = Modifier.width(110.dp)
            )
        }
    }
}

/* ---------------- Final CTA ---------------- */

@Composable
private fun FinalCtaSection(onJoinClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(VidyaCornerTokens.hero)
            .height(440.dp)
    ) {
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida/ADBb0uiTft_c1_2MWVAWhm4Gox-ivfML7QYXvMPpzM8A9tXfKXepAlvpYOWI2PW4VYKTMkkxqyoJvdNqB_4RkM_bitptyKAQevv2M2RXO5AkhEYqdSpODppC4zbkBa67wQV-Y2fnTzxu9mFzmzDLt6cgO_iL9rwKHMDkPBbg2q4V0KQlw2tQvst-vE4izvr5-pNPojgtX1uNI3kXK6qvzGMLXCIgav2X-mKVofJBiwU-XpuR9deYAwIaZiNHEVvn",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.55f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VidyaGradients.ctaOverlay)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Ready to secure the future?",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Join the ecosystem where trust meets technology. 50,000+ parents already trust VidyaSetu.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            EduTrustSecondaryButton(
                text = "Join as a Parent",
                onClick = onJoinClick,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            EduTrustOutlinedButton(
                text = "Register Your School",
                onClick = onJoinClick,
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color.White.copy(alpha = 0.4f),
                contentColor = Color.White
            )
        }
    }
}

/* ---------------- Footer ---------------- */

@Composable
private fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                .compositeOver(MaterialTheme.colorScheme.surface))
            .padding(28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VidyaGradients.brand),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "VidyaSetu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "© 2025 VidyaSetu. Built for modern institutional excellence and parent–child security. Innovating education through trust.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(28.dp))
        FooterLinkSection("PORTALS", listOf("Parent Portal", "Admin Dashboard", "Teacher Console"))
        Spacer(modifier = Modifier.height(20.dp))
        FooterLinkSection("SUPPORT", listOf("Privacy Policy", "Terms of Service", "Help Desk"))
    }
}

@Composable
private fun FooterLinkSection(title: String, links: List<String>) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        links.forEach { link ->
            Text(
                link,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
