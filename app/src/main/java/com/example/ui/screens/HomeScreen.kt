package com.example.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PinnedExamCountdownCard
import com.example.ui.components.SegmentedTabRow
import com.example.ui.components.StatsCardsRow
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.HomeTab
import com.example.viewmodel.StudyViewModel

@Composable
fun HomeScreen(
    viewModel: StudyViewModel,
    onNavigate: (AppScreen) -> Unit,
    onOpenQuickAction: (String) -> Unit
) {
    val homeTab by viewModel.homeTab.collectAsState()
    val userProfile by viewModel.repository.userProfile.collectAsState()
    val subjects by viewModel.repository.subjects.collectAsState()
    val sessions by viewModel.repository.sessions.collectAsState()
    val selectedDate by viewModel.repository.selectedDate.collectAsState()
    val exams by viewModel.repository.exams.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Stats Cards Row
        StatsCardsRow(
            targetHoursText = "${userProfile.dailyTargetMinutes / 60}h 00m",
            studyTimeText = "4h 20m",
            progressPercent = 70,
            streakDays = userProfile.currentStreak,
            levelText = "Lv. ${userProfile.level}",
            xpText = "${userProfile.totalXp} XP"
        )

        // Segmented Tabs
        SegmentedTabRow(
            selectedTab = homeTab,
            onTabSelected = { viewModel.setHomeTab(it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        when (homeTab) {
            HomeTab.OVERVIEW -> {
                OverviewTabContent(
                    exams = exams,
                    selectedDate = selectedDate,
                    onSelectDate = { viewModel.repository.setSelectedDate(it) },
                    onNavigate = onNavigate,
                    onOpenQuickAction = onOpenQuickAction
                )
            }
            HomeTab.CALENDAR -> {
                // Navigate directly to dedicated full-page Calendar
                LaunchedEffect(Unit) {
                    onNavigate(AppScreen.CALENDAR)
                }
                CalendarTabContent(
                    selectedDate = selectedDate,
                    onSelectDate = { viewModel.repository.setSelectedDate(it) },
                    onNavigate = onNavigate
                )
            }
            HomeTab.EVENTS -> {
                EventsTabContent(
                    selectedDate = selectedDate,
                    onOpenQuickAction = onOpenQuickAction
                )
            }
            HomeTab.DAILY_DETAILS -> {
                DailyDetailsTabContent(
                    selectedDate = selectedDate,
                    sessions = sessions
                )
            }
            HomeTab.ANALYTICS -> {
                MonthlySummaryCard(
                    onViewFullAnalytics = { onNavigate(AppScreen.ANALYTICS) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions Row matching screenshot
        QuickActionsBar(onActionClick = onOpenQuickAction)
    }
}

@Composable
private fun OverviewTabContent(
    exams: List<Exam>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onOpenQuickAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. PINNED EXAM COUNTDOWN CARD (Placed directly on Home page as requested)
        PinnedExamCountdownCard(
            exams = exams,
            onViewAllExams = { onNavigate(AppScreen.EXAMS) }
        )

        // 2. Events & Reminders Card
        EventsAndRemindersCard(
            selectedDate = selectedDate,
            onAddClick = { onOpenQuickAction("ADD_TASK") },
            onViewAllClick = { onNavigate(AppScreen.EXAMS) }
        )

        // 3. Daily Details & Timeline Card
        DailyTimelineCard(selectedDate = selectedDate)

        // 4. Monthly Summary Card
        MonthlySummaryCard(
            onViewFullAnalytics = { onNavigate(AppScreen.ANALYTICS) }
        )
    }
}

@Composable
fun MonthlyCalendarCard(
    selectedDate: String,
    onSelectDate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("monthly_calendar_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Calendar Overview",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Month Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = TextSecondary)
                }

                Text(
                    text = "May 2024",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = TextSecondary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .border(1.dp, CardBorderDark, RoundedCornerShape(8.dp))
                            .clickable { onSelectDate("2024-05-08") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Today", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Weekday Header
            val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Days Grid
            val calendarRows = listOf(
                listOf(Pair("28", false), Pair("29", false), Pair("30", false), Pair("1", true), Pair("2", true), Pair("3", true), Pair("4", true)),
                listOf(Pair("5", true), Pair("6", true), Pair("7", true), Pair("8", true), Pair("9", true), Pair("10", true), Pair("11", true)),
                listOf(Pair("12", true), Pair("13", true), Pair("14", true), Pair("15", true), Pair("16", true), Pair("17", true), Pair("18", true)),
                listOf(Pair("19", true), Pair("20", true), Pair("21", true), Pair("22", true), Pair("23", true), Pair("24", true), Pair("25", true)),
                listOf(Pair("26", true), Pair("27", true), Pair("28", true), Pair("29", true), Pair("30", true), Pair("31", true), Pair("1", false))
            )

            calendarRows.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { (day, isCurrentMonth) ->
                        val isDay8 = day == "8" && isCurrentMonth
                        val isDaySelected = isDay8 // 8 May highlighted green circle as in screenshot

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isCurrentMonth) {
                                        val dayPadded = if (day.length == 1) "0$day" else day
                                        onSelectDate("2024-05-$dayPadded")
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isDaySelected) EmeraldGreen else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = if (isDaySelected) Color.White else if (isCurrentMonth) TextPrimary else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            // Dot indicator
                            val dotColor = when (day) {
                                "8" -> EmeraldGreen
                                "1", "18", "21" -> AccentBlue
                                "2", "14", "28" -> AccentPurple
                                "3", "9", "15" -> AccentAmber
                                "5", "11", "17" -> PrimaryCyan
                                "22", "29", "31" -> AccentPink
                                else -> if (isCurrentMonth) Color(0xFF4B5563) else Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Calendar Legend matching screenshot
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LegendDot("Math", AccentBlue)
                LegendDot("Physics", EmeraldGreen)
                LegendDot("Chemistry", AccentAmber)
                LegendDot("Test/Quiz", AccentPurple)
                LegendDot("Task/Other", AccentPink)
                LegendDot("No Study", Color(0xFF4B5563))
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(color))
        Text(text = label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun EventsAndRemindersCard(
    selectedDate: String,
    onAddClick: () -> Unit,
    onViewAllClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("events_reminders_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Events & Reminders (8 May 2024)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+ Add",
                    color = EmeraldGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onAddClick)
                )
            }

            // Event Items from screenshot
            EventRowItem(
                title = "Math Test",
                subtitle = "Time & Work",
                time = "07:00 PM",
                icon = Icons.Default.MenuBook,
                iconColor = AccentBlue
            )

            EventRowItem(
                title = "Physics Quiz",
                subtitle = "Kinematics",
                time = "08:30 PM",
                icon = Icons.Default.School,
                iconColor = EmeraldGreen
            )

            EventRowItem(
                title = "Chemistry Revision",
                subtitle = "Chemical Bonding",
                time = "10:00 PM",
                icon = Icons.Default.Science,
                iconColor = AccentAmber
            )

            EventRowItem(
                title = "Revision",
                subtitle = "Mathematics (Formulas)",
                time = "11:15 PM",
                icon = Icons.Default.Refresh,
                iconColor = AccentPurple
            )

            Divider(color = CardBorderDark, modifier = Modifier.padding(vertical = 4.dp))

            // Upcoming Reminders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Upcoming Reminders", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "View All", color = EmeraldGreen, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onViewAllClick))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tomorrow reminder
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(EmeraldGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text(text = "Tomorrow • 07:00 PM", color = TextSecondary, fontSize = 10.sp)
                            Text(text = "Physics Revision", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Kinematics", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                // Exam countdown
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(AccentPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text(text = "JEE Main", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "56 Days Left", color = AccentPurple, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventRowItem(
    title: String,
    subtitle: String,
    time: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = time, color = TextPrimary, fontSize = 12.sp)
            Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DailyTimelineCard(selectedDate: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("daily_timeline_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Daily Details & Timeline (8 May 2024)",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Sessions from screenshot
            TimelineRowItem("07:00 PM - 08:15 PM", "Mathematics", "Time & Work", "1h 15m", "65%", AccentBlue)
            TimelineRowItem("08:30 PM - 09:30 PM", "Physics", "Kinematics", "1h 00m", "40%", EmeraldGreen)
            TimelineRowItem("10:00 PM - 11:05 PM", "Chemistry", "Chemical Bonding", "1h 05m", "20%", AccentAmber)
            TimelineRowItem("11:15 PM - 12:00 AM", "Revision", "Mathematics (Formulas)", "45m", "80%", AccentPurple)

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = CardBorderDark)
            Spacer(modifier = Modifier.height(10.dp))

            // Bottom metric pills matching screenshot
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill("Total Study Time", "4h 20m", Icons.Default.AccessTime, EmeraldGreen)
                MetricPill("Total Sessions", "4", Icons.Default.CalendarToday, AccentPurple)
                MetricPill("Avg. Focus", "61%", Icons.Default.ShowChart, PrimaryCyan)
                MetricPill("Topics Completed", "5", Icons.Default.CheckCircle, EmeraldGreen)
                MetricPill("Streaks", "8", Icons.Default.LocalFireDepartment, AccentAmber)
                MetricPill("Videos Watched", "8 / 10", Icons.Default.SmartDisplay, EmeraldGreen)
            }
        }
    }
}

@Composable
private fun TimelineRowItem(
    timeRange: String,
    subject: String,
    topic: String,
    duration: String,
    percentage: String,
    nodeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(10.dp).clip(CircleShape).background(nodeColor)
            )
            Text(text = timeRange, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(115.dp))
            Column {
                Text(text = subject, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = topic, color = TextSecondary, fontSize = 10.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = duration, color = TextPrimary, fontSize = 11.sp)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(nodeColor.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = percentage, color = nodeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MetricPill(label: String, value: String, icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, color = TextSecondary, fontSize = 9.sp)
            Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MonthlySummaryCard(onViewFullAnalytics: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("monthly_summary_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Summary (May 2024)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 summary stat boxes matching screenshot
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryStatBox("Total Study Time", "48h 30m", Icons.Default.AccessTime, EmeraldGreen, Modifier.weight(1f))
                SummaryStatBox("Total Sessions", "28", Icons.Default.CalendarToday, AccentBlue, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryStatBox("Avg. Focus", "82%", Icons.Default.GpsFixed, EmeraldGreen, Modifier.weight(1f))
                SummaryStatBox("Topics Completed", "36", Icons.Default.CheckCircleOutline, AccentPurple, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Wise Progress
            Text(text = "Subject Wise Progress", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            SubjectProgressRow("Mathematics", 42, AccentBlue, Icons.Default.MenuBook)
            SubjectProgressRow("Physics", 28, EmeraldGreen, Icons.Default.School)
            SubjectProgressRow("Chemistry", 18, AccentAmber, Icons.Default.Science)
            SubjectProgressRow("Business Studies", 12, AccentPurple, Icons.Default.BusinessCenter)

            Spacer(modifier = Modifier.height(14.dp))

            // Focus Trend (This Week) line chart matching screenshot
            FocusTrendChart()

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onViewFullAnalytics),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "View Full Analytics", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryStatBox(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(text = title, color = TextSecondary, fontSize = 10.sp)
            Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SubjectProgressRow(name: String, percentage: Int, color: Color, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(text = name, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.width(110.dp))
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SurfaceDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = percentage / 100f)
                        .background(color)
                )
            }
        }
        Text(text = "$percentage%", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FocusTrendChart() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Focus Trend (This Week)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val values = listOf(70, 75, 80, 90, 85, 88, 78)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height - 20f
                val spacing = width / (days.size - 1)

                val points = values.mapIndexed { index, value ->
                    val x = index * spacing
                    val y = height - (height * (value - 50) / 50f)
                    Offset(x, y)
                }

                // Draw connecting line
                val path = Path()
                points.forEachIndexed { i, pt ->
                    if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                }
                drawPath(path, color = EmeraldGreen, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                // Draw points and values
                points.forEachIndexed { i, pt ->
                    drawCircle(color = EmeraldGreen, radius = 4.dp.toPx(), center = pt)
                    drawCircle(color = BgDark, radius = 2.dp.toPx(), center = pt)
                }
            }

            // Labels under points
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { i, d ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${values[i]}%", color = EmeraldGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = d, color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsBar(onActionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = "Quick Actions",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton("Study Timer", "Start Focus", Icons.Default.PlayArrow, EmeraldGreen) { onActionClick("TIMER") }
            QuickActionButton("Add Subject", "New Subject", Icons.Default.MenuBook, AccentBlue) { onActionClick("ADD_SUBJECT") }
            QuickActionButton("Add Topic", "New Topic", Icons.Default.ListAlt, EmeraldGreen) { onActionClick("ADD_TOPIC") }
            QuickActionButton("Add Video", "New Video", Icons.Default.SmartDisplay, AccentPurple) { onActionClick("ADD_VIDEO") }
            QuickActionButton("Add Note", "Quick Note", Icons.Default.Description, AccentAmber) { onActionClick("ADD_NOTE") }
        }
    }
}

@Composable
private fun QuickActionButton(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun CalendarTabContent(
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        MonthlyCalendarCard(selectedDate = selectedDate, onSelectDate = onSelectDate)
        DailyTimelineCard(selectedDate = selectedDate)
    }
}

@Composable
private fun EventsTabContent(
    selectedDate: String,
    onOpenQuickAction: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EventsAndRemindersCard(
            selectedDate = selectedDate,
            onAddClick = { onOpenQuickAction("ADD_TASK") },
            onViewAllClick = { onOpenQuickAction("ADD_TASK") }
        )
    }
}

@Composable
private fun DailyDetailsTabContent(
    selectedDate: String,
    sessions: List<StudySession>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DailyTimelineCard(selectedDate = selectedDate)
    }
}
