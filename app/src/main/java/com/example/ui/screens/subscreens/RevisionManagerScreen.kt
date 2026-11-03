package com.example.ui.screens.subscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.Revision
import com.example.data.model.RevisionStatus
import com.example.ui.theme.*
import com.example.viewmodel.StudyViewModel

@Composable
fun RevisionManagerScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val revisions by viewModel.repository.revisions.collectAsState()
    val topics by viewModel.repository.topics.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()
    val selectedDate by viewModel.repository.selectedDate.collectAsState()

    var selectedTab by remember { mutableStateOf("Pending") }
    val tabs = listOf("Pending", "Today", "Completed", "All")

    val filteredRevisions = when (selectedTab) {
        "Pending" -> revisions.filter { it.status == RevisionStatus.PENDING }
        "Today" -> revisions.filter { it.scheduledDate == selectedDate }
        "Completed" -> revisions.filter { it.status == RevisionStatus.COMPLETED }
        else -> revisions
    }

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
                        text = "Spaced Repetition Engine",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+1d, +3d, +7d, +14d, +30d scientific intervals",
                        color = EmeraldGreen,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Intervals Info Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Text(
                text = "💡 Completing a revision automatically schedules the next neural consolidation cycle and awards +60 XP!",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                val isSel = tab == selectedTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) EmeraldGreen.copy(alpha = 0.2f) else SurfaceDark)
                        .border(1.dp, if (isSel) EmeraldGreen else CardBorderDark, RoundedCornerShape(10.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSel) EmeraldGreen else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Revisions List
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredRevisions) { rev ->
                val topic = topics.find { it.id == rev.topicId }
                val subj = subjects.find { it.id == rev.subjectId }
                val isDone = rev.status == RevisionStatus.COMPLETED

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDone) EmeraldGreen.copy(alpha = 0.08f) else CardDark)
                        .border(1.dp, if (isDone) EmeraldGreen.copy(alpha = 0.3f) else CardBorderDark, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = topic?.name ?: "Mathematics Formulas",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentPurple.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Cycle #${rev.revisionNumber}", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "${subj?.name ?: "Subject"} • Scheduled: ${rev.scheduledDate}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            if (rev.notes.isNotEmpty()) {
                                Text(text = rev.notes, color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        if (!isDone) {
                            Button(
                                onClick = { viewModel.repository.completeRevision(rev.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("complete_revision_${rev.id}")
                            ) {
                                Text("Complete (+60 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                Text("Completed", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
