package com.example.ui.components

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
import androidx.compose.material3.*
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val actionType: String
)

@Composable
fun SpeedDialDialog(
    onDismiss: () -> Unit,
    onActionSelected: (String) -> Unit
) {
    val actions = listOf(
        QuickActionItem("Study Timer", "Start Focus", Icons.Default.PlayArrow, EmeraldGreen, "TIMER"),
        QuickActionItem("Add Subject", "New Subject", Icons.Default.Book, AccentBlue, "ADD_SUBJECT"),
        QuickActionItem("Add Topic", "New Topic", Icons.Default.ListAlt, EmeraldGreen, "ADD_TOPIC"),
        QuickActionItem("Add Video", "New Video", Icons.Default.SmartDisplay, AccentPurple, "ADD_VIDEO"),
        QuickActionItem("Add Note", "Quick Note", Icons.Default.Description, AccentAmber, "ADD_NOTE"),
        QuickActionItem("Add Task", "To-Do Item", Icons.Default.CheckCircleOutline, PrimaryCyan, "ADD_TASK"),
        QuickActionItem("Add Revision", "Spaced Review", Icons.Default.Autorenew, AccentRed, "ADD_REVISION"),
        QuickActionItem("Mock Test", "Log Score", Icons.Default.Assessment, AccentPink, "ADD_MOCK")
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Quick Actions",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose an action to record",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(actions) { item ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceDark)
                                .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp))
                                .clickable {
                                    onDismiss()
                                    onActionSelected(item.actionType)
                                }
                                .padding(12.dp)
                                .testTag("quick_action_${item.actionType.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.iconTint.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = item.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.subtitle,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
