package com.example.ui.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Dimens

data class ActivityItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun RecentActivityTimeline() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall)
    ) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Dimens.spaceMedium)
        )
        
        val activities = listOf(
            ActivityItem("1", "Payment Received - Al Barsha", "2 hours ago", Icons.Default.AttachMoney, MaterialTheme.colorScheme.primary),
            ActivityItem("2", "Material Purchased - Cement", "5 hours ago", Icons.Default.Construction, MaterialTheme.colorScheme.tertiary),
            ActivityItem("3", "New Client Added - John Doe", "Yesterday", Icons.Default.Person, MaterialTheme.colorScheme.secondary)
        )

        activities.forEachIndexed { index, activity ->
            TimelineRow(
                activity = activity,
                isLast = index == activities.size - 1
            )
        }
    }
}

@Composable
fun TimelineRow(activity: ActivityItem, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(activity.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = activity.color,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else Dimens.spaceMedium)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = activity.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
