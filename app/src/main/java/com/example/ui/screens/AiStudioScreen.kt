package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.gemini.GeminiApiClient
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.RideNearViewModel

@Composable
fun AiStudioScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(1) } // 0: Transcribe, 1: Chatbot, 2: Live API
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.MAIN_NAV) },
                    modifier = Modifier.testTag("ai_screen_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RideNear AI Assistant",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // 3-Mode Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkNavySurface,
                contentColor = ElectricTeal,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ElectricTeal,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Transcribe", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_transcribe")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Gemini Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_chatbot")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Live Voice", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_live_voice")
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> TranscribeAudioTab(viewModel = viewModel)
                1 -> GeminiChatbotTab(viewModel = viewModel)
                2 -> LiveVoiceTab(viewModel = viewModel)
            }
        }
    }
}

// =========================================================================
// 1. AUDIO TRANSCRIPTION TAB (Model: gemini-3.5-transcribe)
// =========================================================================
@Composable
private fun TranscribeAudioTab(viewModel: RideNearViewModel) {
    val isRecording by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val isTranscribing by viewModel.isTranscribing.collectAsStateWithLifecycle()
    val transcript by viewModel.transcriptionResult.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    val pulseTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Model Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF14243F))
                    .border(1.dp, ElectricTeal.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Powered by model: gemini-3.5-transcribe",
                    color = ElectricTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Voice-to-Text Audio Transcription",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Dictate vehicle inspection notes, peer messages, or rental instructions using your microphone.",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Center Pulsing Recording Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .scale(micScale)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecording) Color(0xFFFF5252) else ElectricTeal
                    )
                    .clickable {
                        if (isRecording) {
                            viewModel.stopAndTranscribeAudio()
                        } else {
                            viewModel.startAudioRecording()
                        }
                    }
                    .testTag("mic_record_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    tint = DeepNavy,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isRecording) "Listening... Tap to stop & transcribe" else "Tap microphone to record speech",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isRecording) Color(0xFFFF6B6B) else Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Test button with sample voice
            OutlinedButton(
                onClick = { viewModel.transcribeSampleAudio() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("test_sample_audio_btn")
            ) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Sample Voice Audio", fontSize = 12.sp)
            }
        }

        // Transcription Result Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transcription Output",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (isTranscribing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ElectricTeal,
                            strokeWidth = 2.dp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyElevated)
                        .padding(12.dp)
                ) {
                    Text(
                        text = transcript ?: "Press the mic and speak, or tap 'Test Sample Voice Audio' to see transcription powered by gemini-3.5-transcribe.",
                        fontSize = 13.sp,
                        color = if (transcript != null) Color.White else Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                }

                if (transcript != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(transcript ?: ""))
                                viewModel.showAlert("Transcript copied to clipboard!")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricTeal,
                                contentColor = DeepNavy
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Text", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. GEMINI MULTI-TURN CHATBOT TAB
// Models: gemini-3.1-pro-preview, gemini-3.5-flash, gemini-3.1-flash-lite
// Maintains conversation history, scrollable thread, system instructions
// =========================================================================
@Composable
private fun GeminiChatbotTab(viewModel: RideNearViewModel) {
    val messages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
    val isThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val currentModel by viewModel.selectedAiModel.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeChatbotRole.collectAsStateWithLifecycle()

    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Model Selection Chips Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Model:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)

                ModelChip(
                    name = "gemini-3.5-flash",
                    label = "General (Flash)",
                    isSelected = currentModel == GeminiApiClient.MODEL_FLASH,
                    onClick = { viewModel.setAiModel(GeminiApiClient.MODEL_FLASH) }
                )
                ModelChip(
                    name = "gemini-3.1-pro-preview",
                    label = "Complex (Pro)",
                    isSelected = currentModel == GeminiApiClient.MODEL_PRO,
                    onClick = { viewModel.setAiModel(GeminiApiClient.MODEL_PRO) }
                )
                ModelChip(
                    name = "gemini-3.1-flash-lite",
                    label = "Fast (Lite)",
                    isSelected = currentModel == GeminiApiClient.MODEL_FLASH_LITE,
                    onClick = { viewModel.setAiModel(GeminiApiClient.MODEL_FLASH_LITE) }
                )
            }

            // Role / System Instruction presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Role:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)

                RoleChip(
                    label = "Concierge",
                    isSelected = activeRole == "Mobility Concierge",
                    onClick = {
                        viewModel.setChatbotRole(
                            "Mobility Concierge",
                            "You are RideNear Concierge. Help renters discover nearby rides, calculate pricing, and explain peer sharing rules."
                        )
                    }
                )
                RoleChip(
                    label = "Inspector",
                    isSelected = activeRole == "Condition Inspector",
                    onClick = {
                        viewModel.setChatbotRole(
                            "Condition Inspector",
                            "You are RideNear Mechanical Inspector. Advise owners and renters on tire pressure, brakes, battery health, and checklist damage verification."
                        )
                    }
                )
                RoleChip(
                    label = "Dispute Mediator",
                    isSelected = activeRole == "Dispute Mediator",
                    onClick = {
                        viewModel.setChatbotRole(
                            "Dispute Mediator",
                            "You are RideNear Escrow & Dispute Resolution specialist. Guide parties objectively on security deposit refund rules, damage evidence, and fair resolution."
                        )
                    }
                )
            }
        }

        // Messages Thread (Scrollable)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(messages) { turn ->
                val isUser = turn.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 290.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isUser) 14.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 14.dp
                                )
                            )
                            .background(if (isUser) ElectricTeal else DarkNavyCard)
                            .border(1.dp, if (isUser) Color.Transparent else Color(0xFF273556), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            if (!isUser) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftPurple, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "RideNear AI • $currentModel",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftPurple
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = turn.text,
                                fontSize = 13.sp,
                                color = if (isUser) DeepNavy else Color.White,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkNavyCard)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = ElectricTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thinking with $currentModel...", fontSize = 12.sp, color = ElectricTeal)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Quick prompts suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "How does the security deposit work?",
                "What's on the bike checklist?",
                "Do cars need a license check?"
            ).forEach { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkNavyElevated)
                        .clickable { viewModel.sendAiChatMessage(prompt) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = prompt, fontSize = 11.sp, color = ElectricTeal)
                }
            }
        }

        // Input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .border(1.dp, Color(0xFF1E2B4D))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Ask RideNear AI ($currentModel)...", color = Color(0xFF64748B), fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input_field"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard,
                    focusedBorderColor = ElectricTeal,
                    unfocusedBorderColor = Color(0xFF273556),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.sendAiChatMessage(inputMessage)
                        inputMessage = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ElectricTeal)
                    .testTag("ai_chat_send_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = DeepNavy, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// =========================================================================
// 3. LIVE VOICE CONVERSATIONS TAB (Model: gemini-3.8-live)
// Real-time conversation with Live API
// =========================================================================
@Composable
private fun LiveVoiceTab(viewModel: RideNearViewModel) {
    val conversation by viewModel.liveConversation.collectAsStateWithLifecycle()
    val isListening by viewModel.isLiveListening.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isLiveProcessing.collectAsStateWithLifecycle()

    val pulseTransition = rememberInfiniteTransition(label = "live_wave")
    val waveScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (isListening) 1.2f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1B1B38))
                    .border(1.dp, SoftPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrightGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live API: gemini-3.8-live (Real-time)",
                        color = SoftPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Real-Time Voice Conversations",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Talk hands-free while riding or inspecting vehicles with instantaneous Gemini Live responses.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Animated Radar Waveform Centerpiece
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .scale(waveScale)
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (isListening) Color(0xFFFF5252) else SoftPurple,
                                Color(0x337667F9),
                                Color.Transparent
                            )
                        )
                    )
                    .clickable { viewModel.toggleLiveListening() }
                    .testTag("live_mic_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isListening) Color(0xFFFF5252) else SoftPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Live Voice",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isListening) "Listening in real-time... Speak now" else if (isProcessing) "Gemini Live responding..." else "Tap orb to speak with gemini-3.8-live",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isListening) Color(0xFFFF6B6B) else if (isProcessing) ElectricTeal else Color.White
            )
        }

        // Live Dialogue Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Live Stream Thread", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = SoftPurple)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkNavyElevated)
                        .padding(10.dp)
                ) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(conversation.takeLast(4)) { turn ->
                            val isUser = turn.role == "user"
                            Text(
                                text = "${if (isUser) "You: " else "gemini-3.8-live: "}${turn.text}",
                                fontSize = 12.sp,
                                color = if (isUser) ElectricTeal else Color(0xFFCBD5E1),
                                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                // Quick test queries for Live API
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { viewModel.sendLiveMessage("Live check: Which bike has the longest range near me?") },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Find Longest Range", fontSize = 10.sp, color = ElectricTeal)
                    }

                    Button(
                        onClick = { viewModel.sendLiveMessage("Live check: What is the unlock code protocol for returning a scooter?") },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Return Protocol", fontSize = 10.sp, color = SoftPurple)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelChip(
    name: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) ElectricTeal else DarkNavyElevated)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) DeepNavy else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun RoleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SoftPurple else DarkNavyElevated)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
