package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Priority
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StudyViewModel

@Composable
fun CalendarScreen(
    viewModel: StudyViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val selectedDate by viewModel.repository.selectedDate.collectAsState()
    val sessions by viewModel.repository.sessions.collectAsState()
    val tasks by viewModel.repository.tasks.collectAsState()
    val exams by viewModel.repository.exams.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()

    var showAddEventDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Page Title & Subtitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Academic Calendar",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unified 4-in-1: Schedule, Timelines & Reminders",
                    color = EmeraldGreen,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddEventDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("calendar_add_event_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Event", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 1. Monthly Calendar Overview Card (Interactive)
        MonthlyCalendarCard(
            selectedDate = selectedDate,
            onSelectDate = { viewModel.repository.setSelectedDate(it) }
        )

        // 2. Events & Reminders Card for Selected Date
        EventsAndRemindersCard(
            selectedDate = selectedDate,
            onAddClick = { showAddEventDialog = true },
            onViewAllClick = { onNavigate(AppScreen.EXAMS) }
        )

        // 3. Daily Details & Chronological Timeline for Selected Date
        DailyTimelineCard(selectedDate = selectedDate)

        // 4. Monthly Summary & Analytics for May 2024
        MonthlySummaryCard(
            onViewFullAnalytics = { onNavigate(AppScreen.ANALYTICS) }
        )
    }

    // Add Event Dialog
    if (showAddEventDialog) {
        var eventTitle by remember { mutableStateOf("") }
        var eventTime by remember { mutableStateOf("07:00 PM") }
        var eventNotes by remember { mutableStateOf("") }
        var selectedSubjId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }

        Dialog(onDismissRequest = { showAddEventDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Event or Reminder", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event Title (e.g. Physics Quiz, Mock Test)") },
                        modifier = Modifier.fillMaxWidth().testTag("event_title_input")
                    )
                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = { eventTime = it },
                        label = { Text("Time (e.g. 08:30 PM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eventNotes,
                        onValueChange = { eventNotes = it },
                        label = { Text("Notes / Topics Covered") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddEventDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (eventTitle.isNotBlank()) {
                                    viewModel.repository.addTask(
                                        title = eventTitle.trim(),
                                        date = selectedDate,
                                        priority = Priority.HIGH,
                                        subjectId = selectedSubjId,
                                        estimatedMinutes = 60
                                    )
                                    showAddEventDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save Event")
                        }
                    }
                }
            }
        }
    }
}
