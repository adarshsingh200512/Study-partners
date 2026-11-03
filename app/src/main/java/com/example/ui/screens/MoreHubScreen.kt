package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StudyViewModel

data class MoreHubModule(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val screen: AppScreen,
    val badge: String? = null
)

@Composable
fun MoreHubScreen(
    viewModel: StudyViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val modules = listOf(
        MoreHubModule("AI Study Coach", "Thinking Mode & Chatbot", Icons.Default.Psychology, PrimaryCyan, AppScreen.AI_COACH, "GEMINI"),
        MoreHubModule("Calendar & Schedule", "Unified 4-in-1 Calendar", Icons.Default.CalendarToday, AccentBlue, AppScreen.CALENDAR),
        MoreHubModule("Smart Planner", "Auto-Schedule & Recovery", Icons.Default.EventNote, EmeraldGreen, AppScreen.SMART_PLANNER, "AUTO"),
        MoreHubModule("Revision Manager", "Spaced Repetition +1d/+3d", Icons.Default.Autorenew, AccentAmber, AppScreen.REVISIONS, "DUE"),
        MoreHubModule("Exam Countdown", "JEE Main & Board Readiness", Icons.Default.School, AccentPurple, AppScreen.EXAMS, "56d"),
        MoreHubModule("Distraction Shield", "App Blocker & Urge Log", Icons.Default.Shield, AccentRed, AppScreen.FOCUS_DISTRACTION_SHIELD),
        MoreHubModule("Daily Missions", "XP Tasks & Milestones", Icons.Default.TrackChanges, EmeraldGreen, AppScreen.DAILY_MISSIONS),
        MoreHubModule("Task Manager", "Prioritized Study To-Dos", Icons.Default.CheckCircleOutline, AccentBlue, AppScreen.TASKS),
        MoreHubModule("Notes Library", "Formulas & Short Tricks", Icons.Default.Description, AccentAmber, AppScreen.NOTES),
        MoreHubModule("Study Materials", "PDFs, Docs & Links", Icons.Default.FolderOpen, PrimaryTeal, AppScreen.MATERIALS),
        MoreHubModule("Question Bank", "PYQs & Practice Quiz Mode", Icons.Default.Quiz, AccentPurple, AppScreen.QUESTIONS_QUIZ),
        MoreHubModule("Formula Library", "Math & Physics Shortcuts", Icons.Default.Functions, AccentBlue, AppScreen.FORMULAS),
        MoreHubModule("Mock Tests", "Scores & Accuracy Tracker", Icons.Default.Assessment, AccentPink, AppScreen.MOCK_TESTS),
        MoreHubModule("Smart Flashcards", "Flip Studio & OCR Generator", Icons.Default.Flip, EmeraldGreen, AppScreen.FLASHCARDS_OCR),
        MoreHubModule("Gamification", "Badges & Level Progress", Icons.Default.EmojiEvents, AccentAmber, AppScreen.GAMIFICATION),
        MoreHubModule("Peer Study Rooms", "Live Focus Rooms & Board", Icons.Default.Groups, PrimaryCyan, AppScreen.PEER_ROOMS),
        MoreHubModule("Backup & Settings", "JSON Export, Import & Themes", Icons.Default.Settings, TextSecondary, AppScreen.BACKUP_SETTINGS)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .padding(bottom = 80.dp)
    ) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(
                text = "Study Tracker Hub",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "All 22 academic modules & productivity tools",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(modules) { mod ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
                        .clickable { onNavigate(mod.screen) }
                        .padding(12.dp)
                        .testTag("hub_item_${mod.screen.name.lowercase()}")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(mod.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = mod.icon,
                                    contentDescription = mod.title,
                                    tint = mod.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (mod.badge != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(mod.color.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = mod.badge,
                                        color = mod.color,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = mod.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = mod.subtitle,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
