package com.example.ui.screens.subscreens

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
import com.example.ui.theme.*
import com.example.viewmodel.StudyViewModel

@Composable
fun ExamsAndMissionsScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val exams by viewModel.repository.exams.collectAsState()
    val missions by viewModel.repository.missions.collectAsState()
    var selectedTab by remember { mutableStateOf("Exams") }

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
                    Text("Exams & Daily Missions", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Countdown, targets & gamified XP rewards", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { selectedTab = if (selectedTab == "Exams") "Missions" else "Exams" },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(if (selectedTab == "Exams") "Daily Missions" else "Exams", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedTab == "Exams") {
            Text("Upcoming Exam Countdowns (${exams.size})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(exams) { exam ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardDark)
                            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = exam.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Date: ${exam.examDate} • ${exam.examTime}", color = TextSecondary, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AccentPurple.copy(alpha = 0.2f))
                                        .border(1.dp, AccentPurple, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("56 Days Left", color = AccentPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(text = "Location: ${exam.location}", color = TextSecondary, fontSize = 11.sp)
                            if (exam.notes.isNotEmpty()) {
                                Text(text = "Strategy: ${exam.notes}", color = EmeraldGreen, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Text("Daily Missions & XP Checklist", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(missions) { mission ->
                    val progress = if (mission.targetValue > 0) (mission.currentValue.toFloat() / mission.targetValue).coerceAtMost(1f) else 0f
                    val isComplete = mission.isCompleted || progress >= 1f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isComplete) EmeraldGreen.copy(alpha = 0.08f) else CardDark)
                            .border(1.dp, if (isComplete) EmeraldGreen.copy(alpha = 0.3f) else CardBorderDark, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isComplete) EmeraldGreen else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(text = mission.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentAmber.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+${mission.xpReward} XP", color = AccentAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${mission.currentValue} / ${mission.targetValue} ${mission.unit}", color = TextSecondary, fontSize = 10.sp)
                                Text(text = "${(progress * 100).toInt()}%", color = if (isComplete) EmeraldGreen else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                color = EmeraldGreen,
                                trackColor = SurfaceDark
                            )
                        }
                    }
                }
            }
        }
    }
}
