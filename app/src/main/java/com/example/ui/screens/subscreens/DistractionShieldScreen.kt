package com.example.ui.screens.subscreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.StudyViewModel

@Composable
fun DistractionShieldScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val blockedApps by viewModel.repository.blockedApps.collectAsState()
    val distractions by viewModel.repository.distractions.collectAsState()

    var strictFocusLock by remember { mutableStateOf(true) }
    var showLogUrgeDialog by remember { mutableStateOf(false) }
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showEmergencyExitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column {
                    Text("Distraction Shield", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("App blocker & urge reflex shield", color = AccentRed, fontSize = 11.sp)
                }
            }

            Button(
                onClick = { showEmergencyExitDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, AccentRed),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.testTag("emergency_exit_btn")
            ) {
                Text("Emergency Exit", color = AccentRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Strict Lock Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Strict Focus Lock", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Blocks distracting notification popups & triggers cool-down reflections when opening blacklisted apps.", color = TextSecondary, fontSize = 11.sp)
                }
                Switch(
                    checked = strictFocusLock,
                    onCheckedChange = { strictFocusLock = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen, checkedTrackColor = EmeraldGreen.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Urge Logger Action Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Feeling tempted to scroll?", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Log your urge and reflect before checking your phone", color = TextSecondary, fontSize = 10.sp)
                }
                Button(
                    onClick = { showLogUrgeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("log_urge_btn")
                ) {
                    Text("Log Urge", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Blocked Apps Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Protected App Blacklist (${blockedApps.size})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("+ Add App", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showAddAppDialog = true })
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(blockedApps) { appName ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
                        }
                        Text(text = appName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    IconButton(
                        onClick = { viewModel.repository.removeBlockedApp(appName) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Log Urge Dialog
    if (showLogUrgeDialog) {
        var appName by remember { mutableStateOf("Instagram") }
        var reflection by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showLogUrgeDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Mindful Distraction Reflection", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Take a deep breath. Why do you feel like checking your phone right now? Boredom? Anxiety? Hard question?", color = TextSecondary, fontSize = 11.sp)

                    OutlinedTextField(
                        value = appName,
                        onValueChange = { appName = it },
                        label = { Text("App you were about to open") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reflection,
                        onValueChange = { reflection = it },
                        label = { Text("Reflection: What should you do instead?") },
                        placeholder = { Text("I will do 5 deep breaths and solve 1 more problem.") },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showLogUrgeDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                viewModel.repository.logDistractionUrge(appName, reflection)
                                showLogUrgeDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("I Will Keep Focusing")
                        }
                    }
                }
            }
        }
    }

    // Add App Dialog
    if (showAddAppDialog) {
        var appName by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddAppDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add App to Distraction Blacklist", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("App Name (e.g. Discord, Twitter)") }, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddAppDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (appName.isNotBlank()) {
                                    viewModel.repository.addBlockedApp(appName.trim())
                                    showAddAppDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }

    // Emergency Exit Dialog
    if (showEmergencyExitDialog) {
        Dialog(onDismissRequest = { showEmergencyExitDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = AccentRed, modifier = Modifier.size(36.dp))
                    Text("Emergency Exit Prompt", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Are you sure you need to exit focus mode? Your 12-day streak and XP multiplier are active. Take 30 seconds before deciding.", color = TextSecondary, fontSize = 11.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showEmergencyExitDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Stay Focused")
                        }
                        Button(
                            onClick = {
                                showEmergencyExitDialog = false
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                            border = BorderStroke(1.dp, CardBorderDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Exit Anyway", color = AccentRed)
                        }
                    }
                }
            }
        }
    }
}
