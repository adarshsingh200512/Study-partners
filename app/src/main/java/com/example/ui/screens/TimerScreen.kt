package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudyMode
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StudyViewModel

@Composable
fun TimerScreen(
    viewModel: StudyViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val isPaused by viewModel.isTimerPaused.collectAsState()
    val secondsLeft by viewModel.timerSecondsLeft.collectAsState()
    val initialSeconds by viewModel.initialTimerSeconds.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val activeSubjectId by viewModel.activeSubjectId.collectAsState()
    val activeTopicId by viewModel.activeTopicId.collectAsState()
    val activeVideoId by viewModel.activeVideoId.collectAsState()
    val showPostSession by viewModel.showPostSessionDialog.collectAsState()

    val subjects by viewModel.repository.subjects.collectAsState()
    val topics by viewModel.repository.topics.collectAsState()
    val videos by viewModel.repository.videos.collectAsState()

    val currentSubject = subjects.find { it.id == activeSubjectId } ?: subjects.firstOrNull()
    val currentTopic = topics.find { it.id == activeTopicId }
    val currentVideo = videos.find { it.id == activeVideoId }

    val mins = secondsLeft / 60
    val secs = secondsLeft % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)
    val progress = if (initialSeconds > 0) 1f - (secondsLeft.toFloat() / initialSeconds) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Study Focus Timer",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pomodoro & Deep Focus Session Tracking",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { onNavigate(AppScreen.FOCUS_DISTRACTION_SHIELD) },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, CardBorderDark),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("zen_focus_mode_btn")
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Distraction Shield", color = TextPrimary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pre-Session Target Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Session Focus Target",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Subject row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Subject:", color = TextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subjects.forEach { s ->
                            val isSel = s.id == currentSubject?.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryTeal.copy(alpha = 0.2f) else SurfaceDark)
                                    .border(1.dp, if (isSel) PrimaryTeal else CardBorderDark, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setActiveSelection(s.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = s.name,
                                    color = if (isSel) PrimaryTeal else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Topic row
                val subjectTopics = topics.filter { it.subjectId == currentSubject?.id }
                if (subjectTopics.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Topic:", color = TextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            subjectTopics.forEach { t ->
                                val isSel = t.id == currentTopic?.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) EmeraldGreen.copy(alpha = 0.2f) else SurfaceDark)
                                        .border(1.dp, if (isSel) EmeraldGreen else CardBorderDark, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setActiveSelection(currentSubject?.id ?: "", t.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = t.name,
                                        color = if (isSel) EmeraldGreen else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Circular Animated Timer Display
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(CardDark)
                .border(6.dp, PrimaryTeal.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = EmeraldGreen,
                strokeWidth = 8.dp,
                trackColor = Color.Transparent
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    color = TextPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("timer_countdown_display")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedMode.title,
                    color = EmeraldGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${currentSubject?.name ?: "Mathematics"} • ${currentTopic?.name ?: "Time & Work"}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Timer Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset button
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceDark).testTag("timer_reset_btn")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextSecondary)
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Main Play/Pause Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(if (isRunning && !isPaused) AccentAmber else EmeraldGreen)
                    .clickable {
                        if (!isRunning) viewModel.startTimer()
                        else if (isPaused) viewModel.resumeTimer()
                        else viewModel.pauseTimer()
                    }
                    .testTag("timer_play_pause_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!isRunning || isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (!isRunning || isPaused) "Start" else "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Stop / Complete Session Button
            IconButton(
                onClick = { viewModel.stopTimer() },
                modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceDark).testTag("timer_stop_btn")
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = AccentRed)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Duration Presets Row
        Text(text = "Duration Presets", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PresetButton("15m", 15) { viewModel.setTimerDurationMinutes(15) }
            PresetButton("25m", 25) { viewModel.setTimerPreset(StudyMode.POMODORO) }
            PresetButton("45m", 45) { viewModel.setTimerDurationMinutes(45) }
            PresetButton("60m", 60) { viewModel.setTimerPreset(StudyMode.DAY) }
            PresetButton("90m", 90) { viewModel.setTimerPreset(StudyMode.DEEP_FOCUS) }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Study Modes Grid (7 Modes from Prompt Section 12)
        Text(text = "7 Configurable Study Modes", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StudyMode.values().forEach { mode ->
                val isSel = mode == selectedMode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) EmeraldGreen.copy(alpha = 0.12f) else CardDark)
                        .border(1.dp, if (isSel) EmeraldGreen else CardBorderDark, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setTimerPreset(mode) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = mode.title, color = if (isSel) EmeraldGreen else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "${mode.workMinutes}m Focus • ${mode.breakMinutes}m Rest", color = TextSecondary, fontSize = 11.sp)
                    }
                    if (isSel) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // Post Session Rating Modal (11 Automatic Actions)
    if (showPostSession) {
        var rating by remember { mutableStateOf(4) }
        var notes by remember { mutableStateOf("") }
        var markVideoDone by remember { mutableStateOf(currentVideo != null && !currentVideo.isCompleted) }

        Dialog(onDismissRequest = { viewModel.dismissPostSession() }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(EmeraldGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(28.dp))
                    }

                    Text("Session Completed!", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("How focused were you during this session?", color = TextSecondary, fontSize = 12.sp)

                    // 1 to 5 Star Rating
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$i Stars",
                                    tint = AccentAmber,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Session Notes / Key Takeaways") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (currentVideo != null && !currentVideo.isCompleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = markVideoDone, onCheckedChange = { markVideoDone = it })
                            Text(text = "Mark '${currentVideo.title}' as watched", color = TextPrimary, fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = "Saving this session will automatically update your Study Time, XP, Subject & Topic hours, Calendar, and Daily Missions!",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            viewModel.completeSession(rating, notes, markVideoDone)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_session_btn")
                    ) {
                        Text("Save & Update All Metrics (+XP)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetButton(text: String, minutes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
