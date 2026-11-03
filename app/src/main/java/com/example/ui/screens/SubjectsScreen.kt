package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StudyViewModel

@Composable
fun SubjectsScreen(
    viewModel: StudyViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val subjects by viewModel.repository.subjects.collectAsState()
    val chapters by viewModel.repository.chapters.collectAsState()
    val topics by viewModel.repository.topics.collectAsState()
    val videos by viewModel.repository.videos.collectAsState()

    var selectedSubjectId by remember { mutableStateOf<String?>(subjects.firstOrNull()?.id) }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showAddVideoDialog by remember { mutableStateOf(false) }

    val activeSubject = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()
    val activeTopic = topics.find { it.id == selectedTopicId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .padding(bottom = 80.dp)
    ) {
        // Top Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Subjects & Syllabus",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage chapters, micro-syllabus & unlimited videos",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddSubjectDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_subject_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Horizontal Subject Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subjects.forEach { subj ->
                val isSelected = subj.id == activeSubject?.id
                val color = Color(android.graphics.Color.parseColor(subj.colorHex))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) color.copy(alpha = 0.2f) else CardDark)
                        .border(1.dp, if (isSelected) color else CardBorderDark, RoundedCornerShape(12.dp))
                        .clickable {
                            selectedSubjectId = subj.id
                            selectedTopicId = null
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = subj.name,
                        color = if (isSelected) color else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeTopic != null) {
            // Topic Details & Unlimited Video Tracking System (Section 7)
            TopicVideoTrackingView(
                topic = activeTopic,
                videos = videos.filter { it.topicId == activeTopic.id },
                onBack = { selectedTopicId = null },
                onAddVideo = { showAddVideoDialog = true },
                onToggleVideo = { videoId, isCompleted ->
                    viewModel.repository.markVideoCompleted(videoId, isCompleted)
                },
                onDeleteVideo = { viewModel.repository.deleteVideo(it) },
                onStartTopicTimer = {
                    viewModel.setActiveSelection(activeTopic.subjectId, activeTopic.id)
                    onNavigate(AppScreen.STUDY_TIMER)
                }
            )
        } else if (activeSubject != null) {
            // Subject Details and Chapters & Topics List
            SubjectDetailView(
                subject = activeSubject,
                chapters = chapters.filter { it.subjectId == activeSubject.id },
                topics = topics.filter { it.subjectId == activeSubject.id },
                videos = videos,
                onSelectTopic = { selectedTopicId = it.id },
                onAddTopic = { showAddTopicDialog = true },
                onStartSubjectTimer = {
                    viewModel.setActiveSelection(activeSubject.id)
                    onNavigate(AppScreen.STUDY_TIMER)
                }
            )
        }
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        var name by remember { mutableStateOf("") }
        var targetHours by remember { mutableStateOf("60") }
        var notes by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddSubjectDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add New Subject", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth().testTag("subject_name_input")
                    )
                    OutlinedTextField(
                        value = targetHours,
                        onValueChange = { targetHours = it },
                        label = { Text("Target Hours") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Exam Target") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    viewModel.repository.addSubject(
                                        name = name.trim(),
                                        colorHex = "#14B8A6",
                                        targetHours = targetHours.toFloatOrNull() ?: 50f,
                                        priority = Priority.HIGH,
                                        notes = notes
                                    )
                                    showAddSubjectDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Add Topic Dialog
    if (showAddTopicDialog && activeSubject != null) {
        var topicName by remember { mutableStateOf("") }
        var estHours by remember { mutableStateOf("12") }
        Dialog(onDismissRequest = { showAddTopicDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Topic to ${activeSubject.name}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = topicName,
                        onValueChange = { topicName = it },
                        label = { Text("Topic / Micro-Syllabus Name") },
                        modifier = Modifier.fillMaxWidth().testTag("topic_name_input")
                    )
                    OutlinedTextField(
                        value = estHours,
                        onValueChange = { estHours = it },
                        label = { Text("Estimated Hours") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddTopicDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (topicName.isNotBlank()) {
                                    val chapter = chapters.find { it.subjectId == activeSubject.id }
                                    viewModel.repository.addTopic(
                                        subjectId = activeSubject.id,
                                        chapterId = chapter?.id ?: "c_default",
                                        name = topicName.trim(),
                                        estimatedHours = estHours.toFloatOrNull() ?: 10f,
                                        priority = Priority.HIGH
                                    )
                                    showAddTopicDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Add Video Dialog
    if (showAddVideoDialog && activeTopic != null) {
        var videoTitle by remember { mutableStateOf("") }
        var duration by remember { mutableStateOf("45") }
        var notes by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddVideoDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Video to ${activeTopic.name}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = videoTitle,
                        onValueChange = { videoTitle = it },
                        label = { Text("Video Lecture Title") },
                        modifier = Modifier.fillMaxWidth().testTag("video_title_input")
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Lecture Key Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddVideoDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (videoTitle.isNotBlank()) {
                                    viewModel.repository.addVideo(
                                        topicId = activeTopic.id,
                                        title = videoTitle.trim(),
                                        durationMinutes = duration.toIntOrNull() ?: 30,
                                        notes = notes
                                    )
                                    showAddVideoDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Add Video")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectDetailView(
    subject: Subject,
    chapters: List<Chapter>,
    topics: List<Topic>,
    videos: List<Video>,
    onSelectTopic: (Topic) -> Unit,
    onAddTopic: () -> Unit,
    onStartSubjectTimer: () -> Unit
) {
    val totalEstHours = topics.sumOf { it.estimatedHours.toDouble() }.toFloat()
    val totalDoneHours = topics.sumOf { it.completedHours.toDouble() }.toFloat()
    val subjectPercent = if (totalEstHours > 0) ((totalDoneHours / totalEstHours) * 100).toInt() else 0
    val subjectVideos = videos.filter { v -> topics.any { t -> t.id == v.topicId } }
    val doneVideos = subjectVideos.count { it.isCompleted }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Subject Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = subject.name, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = subject.description, color = TextSecondary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onStartSubjectTimer,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Focus Timer", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Progress", color = TextSecondary, fontSize = 10.sp)
                        Text("$subjectPercent%", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Logged Hours", color = TextSecondary, fontSize = 10.sp)
                        Text("${"%.1f".format(totalDoneHours)}h / ${"%.0f".format(totalEstHours)}h", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Videos Watched", color = TextSecondary, fontSize = 10.sp)
                        Text("$doneVideos / ${subjectVideos.size}", color = AccentPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Topics Done", color = TextSecondary, fontSize = 10.sp)
                        Text("${topics.count { it.status == TopicStatus.COMPLETED }} / ${topics.size}", color = PrimaryCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Topics & Micro-Syllabus", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("+ Add Topic", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onAddTopic))
        }

        // Topics list
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topics) { topic ->
                val topicVideos = videos.filter { it.topicId == topic.id }
                val completedTopicVideos = topicVideos.count { it.isCompleted }
                val topicPercent = if (topic.estimatedHours > 0) ((topic.completedHours / topic.estimatedHours) * 100).toInt() else 0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                        .clickable { onSelectTopic(topic) }
                        .padding(12.dp)
                        .testTag("topic_item_${topic.id}")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = topic.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = topic.description.ifEmpty { "Micro-syllabus tracking" }, color = TextSecondary, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (topic.status) {
                                            TopicStatus.COMPLETED -> EmeraldGreen.copy(alpha = 0.2f)
                                            TopicStatus.IN_PROGRESS -> PrimaryCyan.copy(alpha = 0.2f)
                                            TopicStatus.REVISION_PENDING -> AccentAmber.copy(alpha = 0.2f)
                                            TopicStatus.NOT_STARTED -> Color.Gray.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = topic.status.name.replace("_", " "),
                                    color = when (topic.status) {
                                        TopicStatus.COMPLETED -> EmeraldGreen
                                        TopicStatus.IN_PROGRESS -> PrimaryCyan
                                        TopicStatus.REVISION_PENDING -> AccentAmber
                                        TopicStatus.NOT_STARTED -> TextSecondary
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Videos: $completedTopicVideos / ${topicVideos.size} • Focus: ${topic.focusRating}★",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "$topicPercent%", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicVideoTrackingView(
    topic: Topic,
    videos: List<Video>,
    onBack: () -> Unit,
    onAddVideo: () -> Unit,
    onToggleVideo: (String, Boolean) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onStartTopicTimer: () -> Unit
) {
    val completedCount = videos.count { it.isCompleted }
    val progress = if (videos.isNotEmpty()) (completedCount.toFloat() / videos.size) else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column {
                    Text(text = topic.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Unlimited Video Tracking System", color = EmeraldGreen, fontSize = 11.sp)
                }
            }

            Button(
                onClick = onStartTopicTimer,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Start Timer", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Video Progress Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Videos: ${videos.size}", color = TextSecondary, fontSize = 11.sp)
                    Text("Completed: $completedCount (${(progress * 100).toInt()}%)", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = EmeraldGreen,
                    trackColor = SurfaceDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Lecture Series (${videos.size} Videos)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = "+ Add Video", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onAddVideo))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videos) { video ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (video.isCompleted) EmeraldGreen.copy(alpha = 0.08f) else SurfaceDark)
                        .border(1.dp, if (video.isCompleted) EmeraldGreen.copy(alpha = 0.3f) else CardBorderDark, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = video.isCompleted,
                                onCheckedChange = { onToggleVideo(video.id, it) },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen),
                                modifier = Modifier.size(28.dp).testTag("video_checkbox_${video.id}")
                            )
                            Column {
                                Text(
                                    text = video.title,
                                    color = if (video.isCompleted) TextPrimary else TextPrimary.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    fontWeight = if (video.isCompleted) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = "${video.durationMinutes} mins • ${if (video.isCompleted) "Watched ${video.watchedDate} at ${video.watchedTime}" else "Unwatched"}",
                                    color = if (video.isCompleted) EmeraldGreen else TextSecondary,
                                    fontSize = 10.sp
                                )
                                if (video.notes.isNotEmpty()) {
                                    Text(text = "Note: ${video.notes}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }

                        IconButton(onClick = { onDeleteVideo(video.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
