package com.example.ui.screens.finance

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.*
import com.example.core.notifications.ReminderWorker
import com.example.core.util.CurrencyUtils
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.components.layout.EmptyState
import com.example.ui.components.layout.ShimmerCardList
import com.example.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentRemindersScreen(
    viewModel: PaymentRemindersViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var reminderTitle by remember { mutableStateOf("") }
    var reminderDays by remember { mutableStateOf("1") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Custom Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedTextField(
                        value = reminderTitle,
                        onValueChange = { reminderTitle = it },
                        label = { Text("Reminder Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reminderDays,
                        onValueChange = { reminderDays = it.filter { c -> c.isDigit() } },
                        label = { Text("Remind in (days)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (reminderTitle.isNotBlank() && reminderDays.isNotBlank()) {
                        val days = reminderDays.toLongOrNull() ?: 1L
                        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                            .setInitialDelay(days, TimeUnit.DAYS)
                            .setInputData(workDataOf("title" to reminderTitle))
                            .addTag("payment_reminder")
                            .build()
                        WorkManager.getInstance(context).enqueue(workRequest)
                        Toast.makeText(context, "Reminder set for $days days", Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                        reminderTitle = ""
                        reminderDays = "1"
                    }
                }) { Text("Set Reminder") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "Payment Reminders",
                showLogo = false,
                onNavigationClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add custom reminder")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> ShimmerCardList(modifier = Modifier.padding(paddingValues))

            uiState.reminders.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.NotificationsActive,
                    title = "No Payment Reminders",
                    description = "All payments are on track. No upcoming deadlines within 90 days.",
                    modifier = Modifier.padding(paddingValues),
                    actionLabel = "Add Custom Reminder",
                    onAction = { showAddDialog = true }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Dimens.marginMobile),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
                ) {
                    // Summary header
                    item {
                        RemindersSummaryCard(
                            overdueCount = uiState.overdueCount,
                            upcomingCount = uiState.upcomingCount,
                            totalPendingPaise = uiState.totalPendingPaise
                        )
                    }

                    // Overdue section
                    val overdue = uiState.reminders.filter { it.isOverdue }
                    if (overdue.isNotEmpty()) {
                        item {
                            Text(
                                "Overdue (${overdue.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(overdue, key = { it.id + "_overdue" }) { reminder ->
                            ReminderCard(reminder = reminder)
                        }
                    }

                    // Upcoming section
                    val upcoming = uiState.reminders.filter { !it.isOverdue }
                    if (upcoming.isNotEmpty()) {
                        item {
                            Text(
                                "Upcoming (${upcoming.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(upcoming, key = { it.id + "_upcoming" }) { reminder ->
                            ReminderCard(reminder = reminder)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RemindersSummaryCard(
    overdueCount: Int,
    upcomingCount: Int,
    totalPendingPaise: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Payment reminders summary: $overdueCount overdue, $upcomingCount upcoming, total pending ${CurrencyUtils.formatPaise(totalPendingPaise)}" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
            Text(
                "Payment Summary",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                SummaryChip(
                    label = "Overdue",
                    value = overdueCount.toString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                SummaryChip(
                    label = "Upcoming",
                    value = upcomingCount.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                SummaryChip(
                    label = "Total Pending",
                    value = CurrencyUtils.formatPaise(totalPendingPaise),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryChip(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), MaterialTheme.shapes.small)
            .padding(Dimens.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ReminderCard(reminder: PaymentReminderItem) {
    val sdf = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isOverdue)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (reminder.isOverdue)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        if (reminder.isOverdue) Icons.Default.Warning else Icons.Default.Schedule,
                        contentDescription = if (reminder.isOverdue) "Overdue payment" else "Upcoming payment",
                        tint = if (reminder.isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reminder.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    reminder.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (reminder.isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Due: ${sdf.format(Date(reminder.dueDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
