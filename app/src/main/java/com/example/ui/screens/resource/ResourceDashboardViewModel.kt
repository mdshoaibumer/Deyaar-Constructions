package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Attendance
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.*
import java.util.Calendar

data class ResourceDashboardUiState(
    val isLoading: Boolean = true,
    val totalWorkers: Int = 0,
    val activeWorkers: Int = 0,
    val presentToday: Int = 0,
    val absentToday: Int = 0,
    val onLeaveToday: Int = 0,
    val weeklyWagesPaise: Long = 0L,
    val monthlyPayrollPaise: Long = 0L,
    val recentClockIns: List<ClockInItem> = emptyList()
)

data class ClockInItem(
    val initial: String,
    val name: String,
    val role: String,
    val time: String,
    val isLate: Boolean
)

class ResourceDashboardViewModel(
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    val uiState: StateFlow<ResourceDashboardUiState> = combine(
        resourceRepository.getAllWorkers(),
        resourceRepository.getAttendanceForDate(todayStart())
    ) { workers, attendance ->
        val activeWorkers = workers.filter { it.status == "ACTIVE" }
        val presentCount = attendance.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = attendance.count { it.status == AttendanceStatus.ABSENT }
        val halfDayCount = attendance.count { it.status == AttendanceStatus.HALF_DAY }

        val dailyWageTotal = activeWorkers.sumOf { it.dailyWagePaise }
        val weeklyWages = dailyWageTotal * 6 // 6 working days
        val monthlyPayroll = dailyWageTotal * 26 // 26 working days

        val recentClockIns = attendance
            .filter { it.status == AttendanceStatus.PRESENT }
            .take(5)
            .map { att ->
                val worker = workers.find { it.id == att.workerId }
                ClockInItem(
                    initial = worker?.fullName?.take(1) ?: "?",
                    name = worker?.fullName ?: "Unknown",
                    role = "${worker?.trade ?: "Worker"} • ID: ${att.workerId.takeLast(4)}",
                    time = formatTime(att.createdAt),
                    isLate = false // Could compare with shift start
                )
            }

        ResourceDashboardUiState(
            isLoading = false,
            totalWorkers = workers.size,
            activeWorkers = activeWorkers.size,
            presentToday = presentCount,
            absentToday = absentCount,
            onLeaveToday = halfDayCount,
            weeklyWagesPaise = weeklyWages,
            monthlyPayrollPaise = monthlyPayroll,
            recentClockIns = recentClockIns
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResourceDashboardUiState()
    )

    private fun todayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

class ResourceDashboardViewModelFactory(
    private val resourceRepository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResourceDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResourceDashboardViewModel(resourceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
