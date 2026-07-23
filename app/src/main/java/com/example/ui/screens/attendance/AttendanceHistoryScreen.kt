package com.example.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Attendance
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import com.example.ui.theme.Dimens
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AttendanceHistoryUiState(
    val worker: Worker? = null,
    val attendanceList: List<Attendance> = emptyList(),
    val isLoading: Boolean = true,
    // Stats
    val totalDays: Int = 0,
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val halfDays: Int = 0,
    val totalOvertimeHours: Double = 0.0
)

class AttendanceHistoryViewModel(
    private val workerId: String,
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceHistoryUiState())
    val uiState: StateFlow<AttendanceHistoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val worker = repository.getWorkerById(workerId)
            _uiState.update { it.copy(worker = worker) }

            repository.getAttendanceForWorker(workerId).collect { attendanceList ->
                val present = attendanceList.count { it.status == AttendanceStatus.PRESENT }
                val absent = attendanceList.count { it.status == AttendanceStatus.ABSENT }
                val halfDay = attendanceList.count { it.status == AttendanceStatus.HALF_DAY }
                val totalOvertime = attendanceList.sumOf { it.overtimeHours }

                _uiState.update {
                    it.copy(
                        attendanceList = attendanceList,
                        isLoading = false,
                        totalDays = attendanceList.size,
                        presentDays = present,
                        absentDays = absent,
                        halfDays = halfDay,
                        totalOvertimeHours = totalOvertime
                    )
                }
            }
        }
    }
}

class AttendanceHistoryViewModelFactory(
    private val workerId: String,
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceHistoryViewModel(workerId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceHistoryScreen(
    viewModel: AttendanceHistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.worker?.fullName ?: "Attendance History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.ShimmerCardList(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(Dimens.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                // Stats Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                            Text("Attendance Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(label = "Total", value = "${uiState.totalDays}", color = MaterialTheme.colorScheme.primary)
                                StatItem(label = "Present", value = "${uiState.presentDays}", color = com.example.ui.theme.DeyaarTheme.colors.success)
                                StatItem(label = "Absent", value = "${uiState.absentDays}", color = MaterialTheme.colorScheme.error)
                                StatItem(label = "Half", value = "${uiState.halfDays}", color = com.example.ui.theme.DeyaarTheme.colors.warning)
                            }
                            if (uiState.totalOvertimeHours > 0) {
                                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                                Text(
                                    text = "Total Overtime: ${uiState.totalOvertimeHours.toInt()} hours",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Text("History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                if (uiState.attendanceList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No attendance records", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(uiState.attendanceList, key = { it.id }) { attendance ->
                        AttendanceHistoryItem(attendance = attendance, dateFormatter = dateFormatter)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun AttendanceHistoryItem(attendance: Attendance, dateFormatter: SimpleDateFormat) {
    val statusColor = when (attendance.status) {
        AttendanceStatus.PRESENT -> com.example.ui.theme.DeyaarTheme.colors.success
        AttendanceStatus.ABSENT -> MaterialTheme.colorScheme.error
        AttendanceStatus.HALF_DAY -> com.example.ui.theme.DeyaarTheme.colors.warning
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormatter.format(Date(attendance.date)),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (attendance.overtimeHours > 0) {
                    Text(
                        text = "OT: ${attendance.overtimeHours.toInt()}h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (!attendance.remarks.isNullOrBlank()) {
                    Text(
                        text = attendance.remarks,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = attendance.status.name.replace("_", " "),
                style = MaterialTheme.typography.labelMedium,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
