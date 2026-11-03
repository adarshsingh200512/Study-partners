package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun TopHeader(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAiCoachClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onResetDemoClick: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // App Icon Avatar matching screenshot
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.5.dp, PrimaryTeal, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_img),
                    contentDescription = "Study Tracker Logo",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Study Tracker",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "All in One",
                    color = EmeraldGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Plan your days • Track your study",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(42.dp).testTag("search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextPrimary
                )
            }

            // Notification Bell with Badge '3'
            Box {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(42.dp).testTag("notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(AccentRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3-dot overflow menu
            Box {
                IconButton(
                    onClick = { showOverflowMenu = true },
                    modifier = Modifier.size(42.dp).testTag("overflow_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = TextPrimary
                    )
                }

                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false },
                    modifier = Modifier.background(CardDark)
                ) {
                    DropdownMenuItem(
                        text = { Text("AI Study Coach", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Psychology, null, tint = PrimaryCyan) },
                        onClick = {
                            showOverflowMenu = false
                            onAiCoachClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Backup & Settings", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Settings, null, tint = TextSecondary) },
                        onClick = {
                            showOverflowMenu = false
                            onSettingsClick()
                        }
                    )
                    Divider(color = CardBorderDark)
                    DropdownMenuItem(
                        text = { Text("Reset to Demo Data", color = AccentAmber) },
                        leadingIcon = { Icon(Icons.Default.Refresh, null, tint = AccentAmber) },
                        onClick = {
                            showOverflowMenu = false
                            onResetDemoClick()
                        }
                    )
                }
            }
        }
    }
}
