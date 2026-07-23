package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Attendance
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import com.example.domain.repository.TransactionRepository
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionCategory
import kotlinx.coroutines.flow.*
import java.util.Calendar

data class PayrollUiState(
    val isLoading: Boolean = true,
    val totalPendingPaise: Long = 0L,
    val totalPaidMtdPaise: Long = 0L,
    val standardHours: Double = 0.0,
    val overtimeHours: Double = 0.0,
    val workerPayrollItems: List<PayrollWorkerItem> = emptyList(),
    val weekLabel: String = ""
)

data class PayrollWorkerItem(
    val initials: String,
    val name: String,
    val id: String,
    val trade: String,
    val hoursWorked: Double,
    val amountPaise: Long,
    val status: PayrollStatus
)

enum class PayrollStatus { PENDING, PAID, FLAGGED }

class PayrollViewModel(
    private val resourceRepository: ResourceRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<PayrollUiState> = combine(
        resourceRepository.getAllWorkers(),
        resourceRepository.getAttendanceForDate(getWeekStart()),
        transactionRepository.getAllTransactions()
    ) { workers, weekAttendance, transactions ->
        val activeWorkers = workers.filter { it.status == "ACTIVE" }
        val labourPayments = transactions.filter {
            it.category == TransactionCategory.LABOUR_PAYMENT && !it.isDeleted
        }

        // Calculate this month's payments
        val monthStart = getMonthStart()
        val paidThisMonth = labourPayments.filter { it.date >= monthStart }.sumOf { it.amountPaise }

        // Calculate pending for active workers
        val weekStart = getWeekStart()
        val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L

        // Build per-worker payroll items
        val payrollItems = activeWorkers.map { worker ->
            val workerAttendance = weekAttendance.filter { it.workerId == worker.id }
            val daysPresent = workerAttendance.count { it.status == AttendanceStatus.PRESENT }
            val halfDays = workerAttendance.count { it.status == AttendanceStatus.HALF_DAY }
            val effectiveDays = daysPresent + (halfDays * 0.5)
            val hoursWorked = effectiveDays * 8.0
            val overtimeHrs = workerAttendance.sumOf { it.overtimeHours }
            val totalHours = hoursWorked + overtimeHrs

            val amountDue = (effectiveDays * worker.dailyWagePaise).toLong() +
                    (overtimeHrs * worker.dailyWagePaise / 8.0 * 1.5).toLong()

            // Check if paid this week
            val isPaid = labourPayments.any {
                it.date in weekStart..weekEnd &&
                it.description?.contains(worker.id) == true
            }

            PayrollWorkerItem(
                initials = worker.fullName.split(" ").map { it.first().uppercase() }.take(2).joinToString(""),
                name = worker.fullName,
                id = "#W-${worker.id.takeLast(4)}",
                trade = worker.trade,
                hoursWorked = totalHours,
                amountPaise = amountDue,
                status = if (isPaid) PayrollStatus.PAID else PayrollStatus.PENDING
            )
        }.sortedByDescending { it.amountPaise }

        val totalPending = payrollItems.filter { it.status == PayrollStatus.PENDING }.sumOf { it.amountPaise }
        val totalStandardHours = payrollItems.sumOf { it.hoursWorked }

        PayrollUiState(
            isLoading = false,
            totalPendingPaise = totalPending,
            totalPaidMtdPaise = paidThisMonth,
            standardHours = totalStandardHours,
            overtimeHours = 0.0, // Simplified
            workerPayrollItems = payrollItems,
            weekLabel = getWeekLabel()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PayrollUiState()
    )

    private fun getWeekStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getMonthStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getWeekLabel(): String {
        val cal = Calendar.getInstance()
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        val sdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
        val weekStart = getWeekStart()
        val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000L
        return "Week $week (${sdf.format(java.util.Date(weekStart))} - ${sdf.format(java.util.Date(weekEnd))})"
    }
}

class PayrollViewModelFactory(
    private val resourceRepository: ResourceRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayrollViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PayrollViewModel(resourceRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
