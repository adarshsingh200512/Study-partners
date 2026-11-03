package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun StatsCardsRow(
    targetHoursText: String = "6h 00m",
    studyTimeText: String = "4h 20m",
    progressPercent: Int = 70,
    streakDays: Int = 12,
    levelText: String = "Lv. 12",
    xpText: String = "1250 XP"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            title = "Today's Target",
            mainValue = targetHoursText,
            subValue = "/ 6h",
            icon = Icons.Default.Adjust,
            iconTint = AccentRed
        )

        StatCard(
            title = "Study Time",
            mainValue = studyTimeText,
            subValue = "/ 6h",
            icon = Icons.Default.AccessTime,
            iconTint = PrimaryCyan
        )

        StatCard(
            title = "Progress",
            mainValue = "$progressPercent%",
            subValue = "",
            icon = Icons.Default.TrendingUp,
            iconTint = EmeraldGreen
        )

        StatCard(
            title = "Streak",
            mainValue = "$streakDays",
            subValue = "Days",
            icon = Icons.Default.LocalFireDepartment,
            iconTint = AccentAmber
        )

        StatCard(
            title = "XP / Level",
            mainValue = levelText,
            subValue = xpText,
            icon = Icons.Default.EmojiEvents,
            iconTint = AccentAmber
        )
    }
}

@Composable
fun StatCard(
    title: String,
    mainValue: String,
    subValue: String,
    icon: ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .width(118.dp)
            .height(78.dp)
            .background(CardDark, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mainValue,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subValue.isNotEmpty()) {
                    Text(
                        text = subValue,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
