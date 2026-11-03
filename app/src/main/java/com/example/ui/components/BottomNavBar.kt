package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen

@Composable
fun BottomNavBar(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp),
        color = CardDark,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavItem(
                title = "Home",
                icon = Icons.Default.Home,
                isSelected = currentScreen == AppScreen.HOME,
                onClick = { onTabSelected(AppScreen.HOME) },
                tag = "nav_home"
            )

            NavItem(
                title = "Calendar",
                icon = Icons.Default.CalendarToday,
                isSelected = currentScreen == AppScreen.CALENDAR,
                onClick = { onTabSelected(AppScreen.CALENDAR) },
                tag = "nav_calendar"
            )

            // Center Circular Green (+) FAB matching screenshot
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen)
                    .clickable(onClick = onFabClick)
                    .testTag("center_fab_action"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            NavItem(
                title = "Subjects",
                icon = Icons.Default.MenuBook,
                isSelected = currentScreen == AppScreen.SUBJECTS,
                onClick = { onTabSelected(AppScreen.SUBJECTS) },
                tag = "nav_subjects"
            )

            NavItem(
                title = "More",
                icon = Icons.Default.Apps,
                isSelected = currentScreen == AppScreen.MORE,
                onClick = { onTabSelected(AppScreen.MORE) },
                tag = "nav_more"
            )
        }
    }
}

@Composable
private fun NavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val tint = if (isSelected) EmeraldGreen else TextSecondary

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
