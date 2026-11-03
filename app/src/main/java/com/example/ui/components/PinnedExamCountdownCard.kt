package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exam
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PinnedExamCountdownCard(
    exams: List<Exam>,
    onViewAllExams: () -> Unit
) {
    var selectedExamIndex by remember { mutableStateOf(0) }
    val exam = exams.getOrNull(selectedExamIndex) ?: exams.firstOrNull() ?: Exam(
        id = "default_exam",
        name = "A Level",
        examDate = "2026-10-03",
        examTime = "07:33 PM"
    )

    // Live ticking countdown state
    var currentTimeMs by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTimeMs = System.currentTimeMillis()
        }
    }

    // Calculate days, hrs, mins, secs until exam
    val (days, hours, minutes, seconds) = remember(exam, currentTimeMs) {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val examDate = format.parse(exam.examDate)
            if (examDate != null) {
                val diff = examDate.time - currentTimeMs
                if (diff > 0) {
                    val d = (diff / (1000 * 60 * 60 * 24)).toInt()
                    val h = ((diff / (1000 * 60 * 60)) % 24).toInt()
                    val m = ((diff / (1000 * 60)) % 60).toInt()
                    val s = ((diff / 1000) % 60).toInt()
                    listOf(d, h, m, s)
                } else {
                    listOf(0, 0, 0, 0)
                }
            } else {
                listOf(29, 23, 59, 4)
            }
        } catch (e: Exception) {
            listOf(29, 23, 59, 4)
        }
    }

    // Format human-readable date
    val formattedExamDate = remember(exam.examDate) {
        try {
            val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val d = inFormat.parse(exam.examDate)
            if (d != null) outFormat.format(d) else "Saturday, October 3, 2026"
        } catch (e: Exception) {
            "Saturday, October 3, 2026"
        }
    }

    // Sky-blue / cyan gradient inspired directly by user's screenshot
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF7DD3FC), // light sky blue top
            Color(0xFF38BDF8), // cyan vibrant center
            Color(0xFF0284C7)  // deep sky blue bottom
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(gradientBrush)
            .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(26.dp))
            .padding(18.dp)
            .testTag("pinned_exam_countdown_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. PINNED COUNTDOWN BADGE
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0F2FE).copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📌",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "PINNED COUNTDOWN",
                        color = Color(0xFF0369A1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // 2. EXAM TITLE (Clickable to switch between exams)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable {
                    if (exams.isNotEmpty()) {
                        selectedExamIndex = (selectedExamIndex + 1) % exams.size
                    }
                }
            ) {
                Text(
                    text = exam.name,
                    color = Color(0xFF0F172A),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                if (exams.size > 1) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Exam",
                        tint = Color(0xFF0369A1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3. GENERAL COUNTDOWN SUB-BADGE
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "GENERAL COUNTDOWN",
                    color = Color(0xFF0C4A6E),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4. 4 FLIP / COUNTER BOXES ROW (DAYS, HRS, MINS, SECS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlipCounterBox(value = String.format("%02d", days), label = "DAYS", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FlipCounterBox(value = String.format("%02d", hours), label = "HRS", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FlipCounterBox(value = String.format("%02d", minutes), label = "MINS", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FlipCounterBox(value = String.format("%02d", seconds), label = "SECS", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 5. BOTTOM INFO ROW: DATE PILL & TIME PILL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = formattedExamDate,
                            color = Color(0xFF0F172A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Time Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = exam.examTime,
                            color = Color(0xFF0F172A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlipCounterBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Counter card with split line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0F2FE))
                .border(1.5.dp, Color(0xFFFEF08A), RoundedCornerShape(16.dp))
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Horizontal split line (classic flip counter)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFBAE6FD))
                    .align(Alignment.Center)
            )

            // Number in dark amber / golden-brown
            Text(
                text = value,
                color = Color(0xFFB45309), // Amber-800
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Label underneath (DAYS, HRS, MINS, SECS)
        Text(
            text = label,
            color = Color(0xFF0C4A6E),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
