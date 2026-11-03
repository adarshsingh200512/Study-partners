package com.example.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.model.Priority
import com.example.data.model.StudyTask
import com.example.data.model.TaskStatus
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StudyViewModel

@Composable
fun SmartPlannerScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val tasks by viewModel.repository.tasks.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()
    val selectedDate by viewModel.repository.selectedDate.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var recoveryMessage by remember { mutableStateOf<String?>(null) }
    var selectedStrategy by remember { mutableStateOf("Weakest Subject First") }

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
                    Text(
                        text = "Smart Study Planner",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Intelligent scheduling & automated recovery",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Smart Recovery Button
            Button(
                onClick = {
                    val recovered = viewModel.repository.runSmartRecovery()
                    recoveryMessage = if (recovered > 0) "✅ Recovered $recovered missed items into upcoming schedule!" else "No overdue items found!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("smart_recovery_btn")
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Smart Recovery", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (recoveryMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldGreen.copy(alpha = 0.2f))
                    .border(1.dp, EmeraldGreen, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = recoveryMessage!!, color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Close, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp).clickable { recoveryMessage = null })
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Auto-generation Strategy Selector
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Auto-Generation Strategy", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Weakest First", "Nearest Exam", "High Priority").forEach { strategy ->
                        val isSel = selectedStrategy.startsWith(strategy.take(4))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PrimaryTeal.copy(alpha = 0.2f) else SurfaceDark)
                                .border(1.dp, if (isSel) PrimaryTeal else CardBorderDark, RoundedCornerShape(8.dp))
                                .clickable { selectedStrategy = strategy }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strategy,
                                color = if (isSel) PrimaryTeal else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Scheduled Study Blocks (${tasks.size})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("+ Add Block", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showAddTaskDialog = true })
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Planner items
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                val subj = subjects.find { it.id == task.subjectId }
                val isDone = task.status == TaskStatus.COMPLETED

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDone) EmeraldGreen.copy(alpha = 0.08f) else SurfaceDark)
                        .border(1.dp, if (isDone) EmeraldGreen.copy(alpha = 0.3f) else CardBorderDark, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { viewModel.repository.toggleTaskStatus(task.id) },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = task.title,
                                    color = if (isDone) TextSecondary else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${subj?.name ?: "General"} • ${task.estimatedMinutes} mins • Due: ${task.deadline}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    viewModel.setActiveSelection(task.subjectId, task.topicId)
                                    viewModel.setTimerDurationMinutes(task.estimatedMinutes)
                                    onNavigate(AppScreen.STUDY_TIMER)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Start Timer", tint = EmeraldGreen)
                            }
                            IconButton(
                                onClick = { viewModel.repository.deleteTask(task.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        var title by remember { mutableStateOf("") }
        var mins by remember { mutableStateOf("45") }
        var deadline by remember { mutableStateOf("09:00 PM") }

        Dialog(onDismissRequest = { showAddTaskDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Planner Study Block", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Study Block Title / Topic") },
                        modifier = Modifier.fillMaxWidth().testTag("planner_title_input")
                    )
                    OutlinedTextField(
                        value = mins,
                        onValueChange = { mins = it },
                        label = { Text("Duration (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Target Time / Deadline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val subjId = subjects.firstOrNull()?.id ?: ""
                                    viewModel.repository.addTask(
                                        title = title.trim(),
                                        date = selectedDate,
                                        priority = Priority.HIGH,
                                        subjectId = subjId,
                                        estimatedMinutes = mins.toIntOrNull() ?: 45
                                    )
                                    showAddTaskDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save Block")
                        }
                    }
                }
            }
        }
    }
}
