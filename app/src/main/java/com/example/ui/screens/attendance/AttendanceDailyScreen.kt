package com.example.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AttendanceStatus
import com.example.ui.components.DeyaarTopAppBar
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
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
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
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                onNavigationClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AttendanceEvent.SaveAll) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = Dimens.spaceLarge).fillMaxWidth(0.6f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.isSaving) "Saving..." else "Save Attendance", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(Dimens.marginMobile),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Deyaar Attendance", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                    Text("Tower B - Formwork Team", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
                Box(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small).clickable { showDatePicker = true }.padding(horizontal = Dimens.spaceMedium, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(dateFormatter.format(Date(uiState.selectedDate)).uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (uiState.saveSuccess) {
                Text(
                    text = "Attendance saved successfully!",
                    color = Color(0xFF10B981),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = Dimens.marginMobile, end = Dimens.marginMobile, bottom = Dimens.spaceSmall)
                )
            }
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = Dimens.marginMobile, end = Dimens.marginMobile, bottom = Dimens.spaceSmall)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, start = Dimens.marginMobile, end = Dimens.marginMobile)
            ) {
                item {
                    // Top Grid (Today's Shift + Calendar)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.spaceLarge),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Today's Shift", style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(uiState.presentCount.toString(), style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("/ ${uiState.attendanceRows.size}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Workers Present", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            
                            val total = maxOf(1, uiState.attendanceRows.size)
                            val pWidth = uiState.presentCount.toFloat() / total
                            val aWidth = uiState.absentCount.toFloat() / total
                            val hWidth = uiState.halfDayCount.toFloat() / total
                            
                            // Progress bar
                            Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small)) {
                                Box(modifier = Modifier.fillMaxHeight().weight(maxOf(pWidth, 0.01f)).background(Color(0xFF0D1C2E)))
                                Box(modifier = Modifier.fillMaxHeight().weight(maxOf(aWidth, 0.01f)).background(Color(0xFFBA1A1A)))
                                Box(modifier = Modifier.fillMaxHeight().weight(maxOf(hWidth, 0.01f)).background(Color(0xFFF97316)))
                                Box(modifier = Modifier.fillMaxHeight().weight(maxOf(1 - pWidth - aWidth - hWidth, 0.01f)).background(Color(0xFFE0E3E5)))
                            }
                            
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                LegendItem(Color(0xFF0D1C2E), uiState.presentCount.toString())
                                LegendItem(Color(0xFFBA1A1A), uiState.absentCount.toString())
                                LegendItem(Color(0xFFF97316), uiState.halfDayCount.toString())
                                LegendItem(Color(0xFF909BB1), (uiState.attendanceRows.size - uiState.presentCount - uiState.absentCount - uiState.halfDayCount).toString())
                            }
                        }
                    }
                }
                
                // Calendar Strip placeholder would go here, skipping for brevity
                item {
                    // Legend full
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.spaceMedium), horizontalArrangement = Arrangement.SpaceEvenly) {
                        LegendItemFull(Color(0xFF0D1C2E), "Present (P)")
                        LegendItemFull(Color(0xFFBA1A1A), "Absent (A)")
                        LegendItemFull(Color(0xFFF97316), "Half Day (H)")
                        LegendItemFull(Color(0xFF909BB1), "Leave (L)")
                    }
                }

                // Worker List Container
                if (uiState.isLoading) {
                    item { com.example.ui.components.layout.ShimmerCardList(itemCount = 4) }
                } else if (uiState.attendanceRows.isEmpty()) {
                    item {
                        com.example.ui.components.layout.EmptyState(
                            icon = Icons.Default.Group,
                            title = "No active workers",
                            description = "Add workers to track attendance."
                        )
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column {
                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow).padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceSmall),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Worker Details", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                                    Text("Attendance Status", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.width(170.dp))
                                }
                            }
                        }
                    }
                    
                    items(uiState.attendanceRows, key = { it.worker.id }) { row ->
                        AttendanceWorkerRow(
                            row = row,
                            onStatusChanged = { status -> viewModel.onEvent(AttendanceEvent.StatusChanged(row.worker.id, status)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LegendItemFull(color: Color, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(value, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AttendanceWorkerRow(
    row: com.example.ui.screens.attendance.WorkerAttendanceRow,
    onStatusChanged: (AttendanceStatus) -> Unit
) {
    val currentStatus = row.attendance?.status
    val isAbsent = currentStatus == AttendanceStatus.ABSENT
    val bg = if (isAbsent) Color(0xFFBA1A1A).copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
    
    Box(modifier = Modifier.fillMaxWidth().background(bg).border(0.5.dp, MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceMedium)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(row.worker.fullName.take(2).uppercase(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                Column {
                    Text(row.worker.fullName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text("EMP-${row.worker.id.takeLast(4)}", style = MaterialTheme.typography.labelMedium, color = if (isAbsent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
                }
            }
            
            // P A H L Buttons
            Row(
                modifier = Modifier.width(170.dp).background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small).padding(2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AttendanceButton("P", currentStatus == AttendanceStatus.PRESENT, Color(0xFF0D1C2E)) { onStatusChanged(AttendanceStatus.PRESENT) }
                AttendanceButton("A", currentStatus == AttendanceStatus.ABSENT, Color(0xFFBA1A1A)) { onStatusChanged(AttendanceStatus.ABSENT) }
                AttendanceButton("H", currentStatus == AttendanceStatus.HALF_DAY, Color(0xFFF97316)) { onStatusChanged(AttendanceStatus.HALF_DAY) }
                AttendanceButton("L", false, Color(0xFF909BB1)) { }
            }
        }
    }
}

@Composable
fun AttendanceButton(text: String, isSelected: Boolean, activeColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp) // Meets minimum 48dp touch target
            .clip(MaterialTheme.shapes.small)
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary)
    }
}
