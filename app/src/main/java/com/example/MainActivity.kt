package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.screens.subscreens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StudyViewModel = viewModel()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val userProfile by viewModel.repository.userProfile.collectAsState()
            val isMiniTimerVisible by viewModel.isMiniTimerVisible.collectAsState()
            val timerSecondsLeft by viewModel.timerSecondsLeft.collectAsState()
            val isTimerPaused by viewModel.isTimerPaused.collectAsState()
            val activeSubjectId by viewModel.activeSubjectId.collectAsState()
            val activeTopicId by viewModel.activeTopicId.collectAsState()
            val subjects by viewModel.repository.subjects.collectAsState()
            val topics by viewModel.repository.topics.collectAsState()
            val showQuickAdd by viewModel.showQuickAddDialog.collectAsState()
            val showAlerts by viewModel.showAlertsDialog.collectAsState()
            val isSearchActive by viewModel.isSearchActive.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()

            val activeSubjName = subjects.find { it.id == activeSubjectId }?.name ?: "Mathematics"
            val activeTopName = topics.find { it.id == activeTopicId }?.name ?: "Time & Work"
            val accentColor = try {
                Color(android.graphics.Color.parseColor(userProfile.accentColorHex))
            } catch (e: Exception) {
                PrimaryTeal
            }

            StudyTrackerTheme(
                darkTheme = userProfile.darkTheme,
                accentColor = accentColor
            ) {
                // Back navigation handler
                if (currentScreen != AppScreen.HOME) {
                    BackHandler {
                        viewModel.navigateBack()
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    topBar = {
                        TopHeader(
                            onMenuClick = { viewModel.navigateTo(AppScreen.MORE) },
                            onSearchClick = { viewModel.setSearchActive(true) },
                            onNotificationClick = { viewModel.toggleAlerts(true) },
                            onAiCoachClick = { viewModel.navigateTo(AppScreen.AI_COACH) },
                            onSettingsClick = { viewModel.navigateTo(AppScreen.BACKUP_SETTINGS) },
                            onResetDemoClick = { viewModel.repository.resetToDemoData() }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onTabSelected = { viewModel.navigateTo(it) },
                            onFabClick = { viewModel.toggleQuickAdd(true) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Main Screen Routing
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) },
                                onOpenQuickAction = { action -> handleQuickAction(action, viewModel) }
                            )
                            AppScreen.CALENDAR -> CalendarScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            AppScreen.SUBJECTS -> SubjectsScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            AppScreen.STUDY_TIMER -> TimerScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            AppScreen.ANALYTICS -> AnalyticsScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            AppScreen.MORE -> MoreHubScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            AppScreen.AI_COACH -> AiCoachScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.SMART_PLANNER, AppScreen.TASKS -> SmartPlannerScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() },
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            AppScreen.REVISIONS -> RevisionManagerScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.EXAMS, AppScreen.DAILY_MISSIONS -> ExamsAndMissionsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.FLASHCARDS_OCR -> FlashcardsAndOcrScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.QUESTIONS_QUIZ, AppScreen.MOCK_TESTS -> QuizAndPracticeScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.NOTES, AppScreen.FORMULAS, AppScreen.MATERIALS -> NotesAndFormulasScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.FOCUS_DISTRACTION_SHIELD -> DistractionShieldScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                            AppScreen.GAMIFICATION, AppScreen.PEER_ROOMS, AppScreen.BACKUP_SETTINGS -> PeerRoomsAndSettingsScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() },
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }

                        // Floating Mini-Timer Overlay
                        if (isMiniTimerVisible) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp)
                            ) {
                                FloatingMiniTimer(
                                    secondsLeft = timerSecondsLeft,
                                    subjectName = activeSubjName,
                                    topicName = activeTopName,
                                    isPaused = isTimerPaused,
                                    onPauseResume = {
                                        if (isTimerPaused) viewModel.resumeTimer() else viewModel.pauseTimer()
                                    },
                                    onStop = { viewModel.stopTimer() },
                                    onExpand = { viewModel.navigateTo(AppScreen.STUDY_TIMER) }
                                )
                            }
                        }

                        // Speed Dial Dialog
                        if (showQuickAdd) {
                            SpeedDialDialog(
                                onDismiss = { viewModel.toggleQuickAdd(false) },
                                onActionSelected = { action -> handleQuickAction(action, viewModel) }
                            )
                        }

                        // Notifications / Alerts Dialog
                        if (showAlerts) {
                            RemindersDialog(onDismiss = { viewModel.toggleAlerts(false) })
                        }

                        // Global Search Dialog
                        if (isSearchActive) {
                            GlobalSearchDialog(
                                query = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                onDismiss = { viewModel.setSearchActive(false) },
                                onSelectResult = { screen ->
                                    viewModel.setSearchActive(false)
                                    viewModel.navigateTo(screen)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleQuickAction(action: String, viewModel: StudyViewModel) {
        when (action) {
            "TIMER" -> viewModel.navigateTo(AppScreen.STUDY_TIMER)
            "ADD_SUBJECT" -> viewModel.navigateTo(AppScreen.SUBJECTS)
            "ADD_TOPIC" -> viewModel.navigateTo(AppScreen.SUBJECTS)
            "ADD_VIDEO" -> viewModel.navigateTo(AppScreen.SUBJECTS)
            "ADD_NOTE" -> viewModel.navigateTo(AppScreen.NOTES)
            "ADD_TASK" -> viewModel.navigateTo(AppScreen.SMART_PLANNER)
            "ADD_REVISION" -> viewModel.navigateTo(AppScreen.REVISIONS)
            "ADD_MOCK" -> viewModel.navigateTo(AppScreen.MOCK_TESTS)
            else -> viewModel.navigateTo(AppScreen.HOME)
        }
    }
}

@Composable
fun RemindersDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reminders & Alerts (3)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = TextSecondary)
                    }
                }

                AlertItem("Math Test: Time & Work", "Tonight at 07:00 PM • 25 practice problems", AccentBlue)
                AlertItem("Physics Quiz: Kinematics", "Tonight at 08:30 PM • Formulas & relative velocity", EmeraldGreen)
                AlertItem("JEE Main Exam in 56 Days", "03 July 2024 • Target: 240+ marks", AccentPurple)

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dismiss All")
                }
            }
        }
    }
}

@Composable
private fun AlertItem(title: String, desc: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(color))
        Column {
            Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun GlobalSearchDialog(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSelectResult: (AppScreen) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Search Study Tracker", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search topics, formulas, notes, tasks...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("global_search_input")
                )

                Text("Quick Jumps", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair("Mathematics", AppScreen.SUBJECTS),
                        Pair("Formulas", AppScreen.FORMULAS),
                        Pair("Flashcards", AppScreen.FLASHCARDS_OCR),
                        Pair("AI Coach", AppScreen.AI_COACH)
                    ).forEach { (label, screen) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceDark)
                                .clickable { onSelectResult(screen) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(label, color = EmeraldGreen, fontSize = 11.sp)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = TextPrimary)
                }
            }
        }
    }
}
