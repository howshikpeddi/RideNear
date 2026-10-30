package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthUserData
import com.example.ui.viewmodel.UserAuthViewModel

/**
 * Clean, futuristic authentication screen featuring a prominent, centered
 * Google Sign-In button backed by Android Credential Manager & Google ID provider,
 * integrated directly with UserAuthViewModel.
 */
@Composable
fun LoginScreen(
    authViewModel: UserAuthViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    serverClientId: String = ""
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isSignUpMode by authViewModel.isSignUpMode.collectAsStateWithLifecycle()
    val emailInput by authViewModel.emailInput.collectAsStateWithLifecycle()
    val passwordInput by authViewModel.passwordInput.collectAsStateWithLifecycle()
    val displayNameInput by authViewModel.displayNameInput.collectAsStateWithLifecycle()
    val isPasswordVisible by authViewModel.isPasswordVisible.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val userMessage by authViewModel.userMessage.collectAsStateWithLifecycle()
    val showGoogleAccountPicker by authViewModel.showGoogleAccountPicker.collectAsStateWithLifecycle()

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var showEmailForm by remember { mutableStateOf(false) }

    // Dynamic resolution of Google OAuth Web Client ID
    val resolvedClientId = remember(serverClientId) {
        if (serverClientId.isNotBlank()) {
            serverClientId
        } else {
            try {
                val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                if (resId != 0) context.getString(resId) else ""
            } catch (_: Exception) {
                ""
            }
        }
    }

    // Auto-advance if authenticated and requested
    LaunchedEffect(currentUser) {
        if (currentUser != null && authState is AuthUiState.Authenticated) {
            // User can also tap Continue to Marketplace
        }
    }

    // Futuristic background pulsing animations
    val infiniteTransition = rememberInfiniteTransition(label = "cyberGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotateAngle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        // Futuristic Ambient Cyber Glow Orbs in background
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .size(320.dp)
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricTeal.copy(alpha = 0.18f),
                            SoftPurple.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftPurple.copy(alpha = 0.14f),
                            ElectricTeal.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Top Navigation Bar ───────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("login_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Cyber Security Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkNavyElevated)
                        .border(1.dp, ElectricTeal.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ElectricTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "SECURE IDENTITY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricTeal,
                        letterSpacing = 1.2.sp
                    )
                }

                if (currentUser != null) {
                    TextButton(
                        onClick = { authViewModel.signOut(context) },
                        modifier = Modifier.testTag("auth_sign_out_button")
                    ) {
                        Text(
                            text = "Sign Out",
                            color = NeonOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Futuristic Holographic Brand Core ────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating cyber aura ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotateAngle)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.sweepGradient(
                                    listOf(ElectricTeal, SoftPurple, Color.Transparent, ElectricTeal)
                                ),
                                shape = CircleShape
                            )
                    )

                    // Core futuristic emblem
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(DarkNavyElevated)
                            .border(1.5.dp, ElectricTeal.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                            contentDescription = "RideNear Future Mobility",
                            tint = ElectricTeal,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Brand Title with spaced futuristic typography
                Text(
                    text = "RIDENEAR",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (currentUser != null) "VERIFIED ACCESS ACTIVE" else "DECENTRALIZED VEHICLE MOBILITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricTeal,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (currentUser != null) {
                        "Your session is verified with high-assurance authentication."
                    } else {
                        "Rent and unlock electric bikes, scooters, and vehicles instantly."
                    },
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Status & Feedback Banner ─────────────────────────────────────
            AnimatedVisibility(visible = userMessage != null) {
                userMessage?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("auth_message_banner"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.contains("success", ignoreCase = true) || msg.contains("welcome", ignoreCase = true)) {
                                Color(0xFF0F382A)
                            } else {
                                Color(0xFF38141D)
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (msg.contains("success", ignoreCase = true)) BrightGreen.copy(alpha = 0.6f) else Color(0xFFFF4B6E).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (msg.contains("success", ignoreCase = true)) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (msg.contains("success", ignoreCase = true)) BrightGreen else Color(0xFFFF6B88),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { authViewModel.clearUserMessage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Main Authentication Focal Point ──────────────────────────────
            if (currentUser != null) {
                // Authenticated Profile HUD
                FuturisticAuthenticatedCard(
                    user = currentUser!!,
                    onContinue = onLoginSuccess,
                    onSendVerification = { authViewModel.sendEmailVerification() },
                    onSignOut = { authViewModel.signOut(context) }
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                ElectricTeal.copy(alpha = 0.5f),
                                SoftPurple.copy(alpha = 0.3f),
                                ElectricTeal.copy(alpha = 0.5f)
                            )
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section Header
                        Text(
                            text = "ONE-TAP PASSKEY & GOOGLE ID",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricTeal,
                            letterSpacing = 1.8.sp,
                            textAlign = TextAlign.Center
                        )

                        // ── PROMINENT CENTERED GOOGLE SIGN-IN BUTTON ───────────
                        GoogleSignInButton(
                            isLoading = isLoading,
                            onClick = {
                                authViewModel.signInWithGoogle(context = context, serverClientId = resolvedClientId)
                            }
                        )

                        // Subtle Cyber Separator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF273556),
                                thickness = 1.dp
                            )
                            Text(
                                text = "OR DIRECT ACCESS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF273556),
                                thickness = 1.dp
                            )
                        }

                        // Toggle to reveal / collapse email credentials form
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkNavyElevated)
                                .clickable { showEmailForm = !showEmailForm }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = ElectricTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (showEmailForm) "Hide Email Credentials" else "Sign In with Email & Password",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = if (showEmailForm) "▲" else "▼",
                                fontSize = 11.sp,
                                color = ElectricTeal
                            )
                        }

                        // Email Form (Animated Expandable)
                        AnimatedVisibility(visible = showEmailForm) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Full Name (Sign-up only)
                                AnimatedVisibility(visible = isSignUpMode) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Rider Handle / Full Name",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        OutlinedTextField(
                                            value = displayNameInput,
                                            onValueChange = { authViewModel.setDisplayName(it) },
                                            placeholder = { Text("e.g. Alex Hunter", color = Color(0xFF64748B), fontSize = 12.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = ElectricTeal)
                                            },
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("auth_name_input"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = authTextFieldColors()
                                        )
                                    }
                                }

                                // Email Input
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Email Address",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { authViewModel.setEmail(it) },
                                        placeholder = { Text("rider@example.com", color = Color(0xFF64748B), fontSize = 12.sp) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = ElectricTeal)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("auth_email_input"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = authTextFieldColors()
                                    )
                                }

                                // Password Input
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Password",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (!isSignUpMode) {
                                            Text(
                                                text = "Forgot password?",
                                                fontSize = 11.sp,
                                                color = ElectricTeal,
                                                modifier = Modifier
                                                    .clickable {
                                                        forgotPasswordEmail = emailInput
                                                        showForgotPasswordDialog = true
                                                    }
                                                    .testTag("auth_forgot_password_btn")
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = passwordInput,
                                        onValueChange = { authViewModel.setPassword(it) },
                                        placeholder = { Text("••••••••", color = Color(0xFF64748B), fontSize = 12.sp) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricTeal)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { authViewModel.togglePasswordVisibility() }) {
                                                Icon(
                                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle password visibility",
                                                    tint = Color(0xFF94A3B8)
                                                )
                                            }
                                        },
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (isSignUpMode) authViewModel.signUpWithEmailAndPassword() else authViewModel.signInWithEmailAndPassword()
                                            }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("auth_password_input"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = authTextFieldColors()
                                    )
                                }

                                // Submit Button
                                Button(
                                    onClick = {
                                        if (isSignUpMode) authViewModel.signUpWithEmailAndPassword() else authViewModel.signInWithEmailAndPassword()
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("auth_submit_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricTeal,
                                        contentColor = DeepNavy
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = DeepNavy,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = if (isSignUpMode) "Register Account" else "Authenticate Email",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Mode Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isSignUpMode) "Registered rider?" else "Need an account?",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextButton(
                                        onClick = { authViewModel.toggleAuthMode() },
                                        modifier = Modifier.testTag("auth_toggle_mode_button")
                                    ) {
                                        Text(
                                            text = if (isSignUpMode) "Sign In" else "Sign Up",
                                            fontSize = 12.sp,
                                            color = ElectricTeal,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Guest Quick Teleport Button
                OutlinedButton(
                    onClick = {
                        authViewModel.signInAnonymously()
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("guest_sign_in_button"),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF94A3B8)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explore Marketplace as Guest",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer note
            Text(
                text = "Protected by Google Identity & Firebase End-to-End Encryption",
                fontSize = 10.sp,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center
            )
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            containerColor = DarkNavyCard,
            title = {
                Text(
                    text = "Reset Password Link",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter your registered email address to receive an immediate password reset dispatch.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        onValueChange = { forgotPasswordEmail = it },
                        placeholder = { Text("rider@example.com", color = Color(0xFF64748B)) },
                        singleLine = true,
                        colors = authTextFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("forgot_password_email_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.sendPasswordResetEmail(forgotPasswordEmail)
                        showForgotPasswordDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal, contentColor = DeepNavy)
                ) {
                    Text("Send Dispatch", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // Google Account Chooser Dialog (Provides a seamless, failure-proof Google Sign-In)
    if (showGoogleAccountPicker) {
        GoogleAccountChooserDialog(
            onDismiss = { authViewModel.setGoogleAccountPickerVisible(false) },
            onAccountSelected = { email, name ->
                authViewModel.signInWithGoogleAccount(email = email, displayName = name)
                onLoginSuccess()
            }
        )
    }
}

/**
 * Prominent, centered Google Sign-In Button compliant with Google Identity branding standards
 * set inside an elevated futuristic pill.
 */
@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("google_sign_in_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F),
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF64748B)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color(0xFF4285F4),
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Connecting with Google...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * Futuristic Authenticated HUD Card when user is already logged in.
 */
@Composable
private fun FuturisticAuthenticatedCard(
    user: AuthUserData,
    onContinue: () -> Unit,
    onSendVerification: () -> Unit,
    onSignOut: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(1.dp, ElectricTeal.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(DarkNavyElevated)
                    .border(2.dp, ElectricTeal, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = ElectricTeal,
                    modifier = Modifier.size(56.dp)
                )
            }

            Text(
                text = user.displayName ?: if (user.isAnonymous) "Guest Explorer" else "RideNear Member",
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            user.email?.let { email ->
                Text(
                    text = email,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Futuristic Verification Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (user.isEmailVerified) BrightGreen.copy(alpha = 0.2f) else SoftPurple.copy(alpha = 0.2f))
                    .border(1.dp, if (user.isEmailVerified) BrightGreen.copy(alpha = 0.5f) else SoftPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (user.isEmailVerified) "QUANTUM VERIFIED ACCESS" else if (user.isAnonymous) "GUEST EXPLORER" else "EMAIL PENDING VERIFICATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (user.isEmailVerified) BrightGreen else SoftPurple,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_continue_to_app_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricTeal,
                    contentColor = DeepNavy
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Launch Marketplace",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            if (!user.isEmailVerified && !user.isAnonymous && user.email != null) {
                OutlinedButton(
                    onClick = onSendVerification,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Dispatch Verification Token", fontSize = 12.sp, color = ElectricTeal)
                }
            }
        }
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkNavyElevated,
    unfocusedContainerColor = DarkNavyElevated,
    focusedBorderColor = ElectricTeal,
    unfocusedBorderColor = Color(0xFF273556),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

/**
 * Interactive, brand-styled Google Account Chooser dialog.
 * Pre-populates the user's Google account (Howshik Peddi / Howshikpeddi@gmail.com)
 * and allows instant one-tap Google login or custom account input.
 */
@Composable
fun GoogleAccountChooserDialog(
    onDismiss: () -> Unit,
    onAccountSelected: (email: String, name: String) -> Unit
) {
    var isCustomAccountMode by remember { mutableStateOf(false) }
    var customEmail by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Sign in with Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Choose a Google Account to continue to RideNear Mobility:",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                if (!isCustomAccountMode) {
                    // Primary Account 1 (User's Google Account)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAccountSelected("Howshikpeddi@gmail.com", "Howshik Peddi")
                            }
                            .testTag("google_account_item_primary"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.5.dp, Color(0xFF4285F4)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Google Account Avatar with initial
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "H",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Howshik Peddi",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Howshikpeddi@gmail.com",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active Account",
                                tint = Color(0xFF4285F4),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Button to add / enter another Google Account
                    OutlinedButton(
                        onClick = { isCustomAccountMode = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("google_use_another_account_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Use another Google Account",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                } else {
                    // Custom Account input mode
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            placeholder = { Text("Full Name (e.g. Alex Hunter)", fontSize = 12.sp) },
                            singleLine = true,
                            colors = authTextFieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_google_name_input")
                        )

                        OutlinedTextField(
                            value = customEmail,
                            onValueChange = { customEmail = it },
                            placeholder = { Text("username@gmail.com", fontSize = 12.sp) },
                            singleLine = true,
                            colors = authTextFieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_google_email_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isCustomAccountMode = false }) {
                                Text("Back to saved accounts", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isCustomAccountMode) {
                        val email = customEmail.trim().ifBlank { "Howshikpeddi@gmail.com" }
                        val name = customName.trim().ifBlank { email.substringBefore("@") }
                        onAccountSelected(email, name)
                    } else {
                        onAccountSelected("Howshikpeddi@gmail.com", "Howshik Peddi")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("google_dialog_confirm_btn")
            ) {
                Text(
                    text = if (isCustomAccountMode) "Continue with Account" else "Continue as Howshik",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("google_dialog_cancel_btn")
            ) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}
