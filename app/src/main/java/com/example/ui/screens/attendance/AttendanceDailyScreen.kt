package com.example.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AttendanceStatus
import com.example.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDailyScreen(
    viewModel: AttendanceViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault()) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Normalize to start of day
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = millis
                        cal.set(Calendar.HOUR_OF_DAY, 0)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        viewModel.onEvent(AttendanceEvent.DateChanged(cal.timeInMillis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Attendance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(AttendanceEvent.SaveAll) },
                        enabled = !uiState.isSaving
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save All")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Date Selector Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spaceMedium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(Dimens.spaceMedium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = dateFormatter.format(Date(uiState.selectedDate)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Tap to change date",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Change date",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Statistics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spaceMedium),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Present",
                    count = uiState.presentCount,
                    color = com.example.ui.theme.DeyaarTheme.colors.success
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Absent",
                    count = uiState.absentCount,
                    color = MaterialTheme.colorScheme.error
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Half Day",
                    count = uiState.halfDayCount,
                    color = com.example.ui.theme.DeyaarTheme.colors.warning
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            // Success message
            if (uiState.saveSuccess) {
                Text(
                    text = "Attendance saved successfully!",
                    color = com.example.ui.theme.DeyaarTheme.colors.success,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = Dimens.spaceMedium)
                )
            }
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = Dimens.spaceMedium)
                )
            }

            // Worker List
            if (uiState.isLoading) {
                com.example.ui.components.layout.ShimmerCardList(itemCount = 4)
            } else if (uiState.attendanceRows.isEmpty()) {
                com.example.ui.components.layout.EmptyState(
                    icon = Icons.Default.Group,
                    title = "No active workers",
                    description = "Add workers in the Resources section to start tracking attendance."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Dimens.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                ) {
                    items(uiState.attendanceRows, key = { it.worker.id }) { row ->
                        AttendanceWorkerCard(
                            row = row,
                            onStatusChanged = { status ->
                                viewModel.onEvent(AttendanceEvent.StatusChanged(row.worker.id, status))
                            },
                            onOvertimeChanged = { hours ->
                                viewModel.onEvent(AttendanceEvent.OvertimeChanged(row.worker.id, hours))
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(64.dp)) }
                }
            }
        }
    }
}

@Composable
fun StatChip(modifier: Modifier = Modifier, label: String, count: Int, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(Dimens.radiusMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun AttendanceWorkerCard(
    row: WorkerAttendanceRow,
    onStatusChanged: (AttendanceStatus) -> Unit,
    onOvertimeChanged: (Double) -> Unit
) {
    val currentStatus = row.attendance?.status
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            // Worker name and trade
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.worker.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = row.worker.trade,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            // Status buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                StatusButton(
                    modifier = Modifier.weight(1f),
                    text = "Present",
                    isSelected = currentStatus == AttendanceStatus.PRESENT,
                    color = com.example.ui.theme.DeyaarTheme.colors.success,
                    onClick = { onStatusChanged(AttendanceStatus.PRESENT) }
                )
                StatusButton(
                    modifier = Modifier.weight(1f),
                    text = "Absent",
                    isSelected = currentStatus == AttendanceStatus.ABSENT,
                    color = MaterialTheme.colorScheme.error,
                    onClick = { onStatusChanged(AttendanceStatus.ABSENT) }
                )
                StatusButton(
                    modifier = Modifier.weight(1f),
                    text = "Half Day",
                    isSelected = currentStatus == AttendanceStatus.HALF_DAY,
                    color = com.example.ui.theme.DeyaarTheme.colors.warning,
                    onClick = { onStatusChanged(AttendanceStatus.HALF_DAY) }
                )
            }

            // Overtime (only visible if present)
            if (currentStatus == AttendanceStatus.PRESENT) {
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Overtime:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                    val overtimeHours = row.attendance?.overtimeHours ?: 0.0
                    IconButton(onClick = {
                        if (overtimeHours > 0) onOvertimeChanged(overtimeHours - 1.0)
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "${overtimeHours.toInt()}h",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        onOvertimeChanged(overtimeHours + 1.0)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(Dimens.radiusSmall)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}
