package com.littlebridge.vidyaprayag.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.vidyaprayag.ui.components.EduTrustGradientButton
import com.littlebridge.vidyaprayag.ui.components.pressScale
import com.littlebridge.vidyaprayag.ui.theme.Motion
import com.littlebridge.vidyaprayag.ui.theme.VidyaCornerTokens
import com.littlebridge.vidyaprayag.ui.theme.VidyaGradients

enum class AuthRole {
    ADMIN, PARENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var role by remember { mutableStateOf(AuthRole.ADMIN) }
    var isOtpSent by remember { mutableStateOf(false) }
    var contactInfo by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(48.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = VidyaCornerTokens.sheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated header icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(VidyaGradients.brandSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Access VidyaSetu",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            AnimatedContent(
                targetState = isOtpSent,
                transitionSpec = {
                    (fadeIn(Motion.tweenIos(Motion.Duration.Short)))
                        .togetherWith(fadeOut(Motion.tweenIos(Motion.Duration.Quick)))
                },
                label = "auth-subtitle"
            ) { sent ->
                Text(
                    text = if (sent) "Verify your identity" else "Enter your credentials to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedContent(
                targetState = isOtpSent,
                transitionSpec = {
                    if (targetState) {
                        // Forward: incoming OTP slides from right
                        (fadeIn(Motion.tweenIos(Motion.Duration.Medium)) +
                            slideInHorizontally(
                                animationSpec = tween(Motion.Duration.Medium, easing = Motion.IOSEase)
                            ) { it / 3 })
                            .togetherWith(
                                fadeOut(Motion.tweenIos(Motion.Duration.Quick)) +
                                    slideOutHorizontally(
                                        animationSpec = tween(Motion.Duration.Quick, easing = Motion.IOSEase)
                                    ) { -it / 3 }
                            )
                    } else {
                        // Back: incoming Entry slides from left
                        (fadeIn(Motion.tweenIos(Motion.Duration.Medium)) +
                            slideInHorizontally(
                                animationSpec = tween(Motion.Duration.Medium, easing = Motion.IOSEase)
                            ) { -it / 3 })
                            .togetherWith(
                                fadeOut(Motion.tweenIos(Motion.Duration.Quick)) +
                                    slideOutHorizontally(
                                        animationSpec = tween(Motion.Duration.Quick, easing = Motion.IOSEase)
                                    ) { it / 3 }
                            )
                    }
                },
                label = "auth-flow"
            ) { showOtp ->
                if (!showOtp) {
                    EntrySection(
                        role = role,
                        onRoleChange = { role = it },
                        contactInfo = contactInfo,
                        onContactInfoChange = { contactInfo = it },
                        onContinue = { isOtpSent = true }
                    )
                } else {
                    OtpSection(
                        contactInfo = contactInfo,
                        otp = otp,
                        onOtpChange = { otp = it.take(6).filter { ch -> ch.isDigit() } },
                        onVerify = { /* Handle login (Phase 2) */ },
                        onBack = { isOtpSent = false; otp = "" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "By continuing, you agree to VidyaSetu's Terms of Service and Privacy Policy.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

/* ------------------ Entry Section ------------------ */

@Composable
private fun EntrySection(
    role: AuthRole,
    onRoleChange: (AuthRole) -> Unit,
    contactInfo: String,
    onContactInfoChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedRoleToggle(
            role = role,
            onRoleChange = onRoleChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Email or Mobile Number",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = contactInfo,
            onValueChange = onContactInfoChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    if (role == AuthRole.ADMIN) "admin@academy.edu" else "+91 98xxx xxxxx",
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Mail, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            },
            shape = VidyaCornerTokens.field,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        EduTrustGradientButton(
            text = "Continue",
            onClick = onContinue,
            enabled = contactInfo.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "TRUSTED SECURITY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp),
                letterSpacing = 1.sp
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialButton(
                text = "Google",
                icon = Icons.Default.AccountCircle,
                modifier = Modifier.weight(1f)
            )
            SocialButton(
                text = "Apple ID",
                icon = Icons.Default.Smartphone,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnimatedRoleToggle(
    role: AuthRole,
    onRoleChange: (AuthRole) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VidyaCornerTokens.field)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                VidyaCornerTokens.field
            )
            .padding(4.dp)
    ) {
        RoleToggleButton(
            text = "Administrator",
            icon = Icons.Default.SupervisorAccount,
            isSelected = role == AuthRole.ADMIN,
            onClick = { onRoleChange(AuthRole.ADMIN) },
            modifier = Modifier.weight(1f)
        )
        RoleToggleButton(
            text = "Parent",
            icon = Icons.Default.FamilyRestroom,
            isSelected = role == AuthRole.PARENT,
            onClick = { onRoleChange(AuthRole.PARENT) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RoleToggleButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        animationSpec = Motion.tweenEmphasized(),
        label = "role-bg"
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = Motion.tweenEmphasized(),
        label = "role-tint"
    )
    val elevationDp by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = Motion.tweenEmphasized(),
        label = "role-elev"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg, RoundedCornerShape(12.dp))
            .border(
                BorderStroke(
                    width = if (isSelected) 1.dp else 0.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isSelected) 0.4f else 0f)
                ),
                RoundedCornerShape(12.dp)
            )
            .pressScale(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SocialButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(VidyaCornerTokens.field)
            .background(Color.Transparent, VidyaCornerTokens.field)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                VidyaCornerTokens.field
            )
            .pressScale(onClick = {}),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

/* ------------------ OTP Section ------------------ */

@Composable
private fun OtpSection(
    contactInfo: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "We've sent a 6-digit code to $contactInfo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Hidden text field handles input + system keyboard.
        Box {
            OutlinedTextField(
                value = otp,
                onValueChange = onOtpChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(6) { index ->
                    OtpField(
                        digit = otp.getOrNull(index)?.toString() ?: "",
                        focused = otp.length == index
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        EduTrustGradientButton(
            text = "Verify & Secure Access",
            onClick = onVerify,
            enabled = otp.length == 6,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = {}) {
            Text(
                "Resend code",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = onBack) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Back to entry", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun OtpField(digit: String, focused: Boolean) {
    val borderColor by animateColorAsState(
        targetValue = if (focused) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = Motion.tweenEmphasized(),
        label = "otp-border"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (focused) 2.dp else 1.dp,
        animationSpec = Motion.tweenEmphasized(),
        label = "otp-border-w"
    )
    val scale by animateFloatAsState(
        targetValue = if (digit.isNotEmpty()) 1f else if (focused) 1.04f else 1f,
        animationSpec = Motion.springQuick(),
        label = "otp-scale"
    )

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                RoundedCornerShape(14.dp)
            )
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = digit,
            transitionSpec = {
                (fadeIn(Motion.tweenIos(Motion.Duration.Quick)))
                    .togetherWith(fadeOut(Motion.tweenIos(Motion.Duration.Quick)))
            },
            label = "otp-digit"
        ) { value ->
            if (value.isEmpty()) {
                Text(
                    "•",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            } else {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
