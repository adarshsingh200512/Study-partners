package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.viewmodel.HomeTab

@Composable
fun SegmentedTabRow(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabItem(
            title = "Overview",
            icon = Icons.Default.GridView,
            isSelected = selectedTab == HomeTab.OVERVIEW,
            onClick = { onTabSelected(HomeTab.OVERVIEW) },
            tag = "tab_overview"
        )
        TabItem(
            title = "Calendar",
            icon = Icons.Default.CalendarToday,
            isSelected = selectedTab == HomeTab.CALENDAR,
            onClick = { onTabSelected(HomeTab.CALENDAR) },
            tag = "tab_calendar"
        )
        TabItem(
            title = "Events",
            icon = Icons.Default.NotificationsNone,
            isSelected = selectedTab == HomeTab.EVENTS,
            onClick = { onTabSelected(HomeTab.EVENTS) },
            tag = "tab_events"
        )
        TabItem(
            title = "Daily Details",
            icon = Icons.Default.FormatListBulleted,
            isSelected = selectedTab == HomeTab.DAILY_DETAILS,
            onClick = { onTabSelected(HomeTab.DAILY_DETAILS) },
            tag = "tab_daily_details"
        )
        TabItem(
            title = "Analytics",
            icon = Icons.Default.PieChart,
            isSelected = selectedTab == HomeTab.ANALYTICS,
            onClick = { onTabSelected(HomeTab.ANALYTICS) },
            tag = "tab_analytics"
        )
    }
}

@Composable
private fun TabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val bgColor = if (isSelected) PrimaryTeal.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) EmeraldGreen else TextSecondary
    val borderColor = if (isSelected) EmeraldGreen.copy(alpha = 0.5f) else CardBorderDark

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
