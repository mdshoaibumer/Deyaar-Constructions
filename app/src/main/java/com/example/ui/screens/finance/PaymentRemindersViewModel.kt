package com.example.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class PaymentReminderItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val dueDate: Long,
    val isOverdue: Boolean,
    val projectId: String,
    val amountPaise: Long
)

data class PaymentRemindersUiState(
    val isLoading: Boolean = true,
    val reminders: List<PaymentReminderItem> = emptyList(),
    val overdueCount: Int = 0,
    val upcomingCount: Int = 0,
    val totalPendingPaise: Long = 0L
)

class PaymentRemindersViewModel(
    private val projectRepository: ProjectRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentRemindersUiState())
    val uiState: StateFlow<PaymentRemindersUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            projectRepository.getAllProjects()
                .combine(transactionRepository.getAllTransactions()) { projects, transactions ->
                    val now = System.currentTimeMillis()
                    val reminders = mutableListOf<PaymentReminderItem>()

                    projects
                        .filter { it.status == ProjectStatus.ACTIVE || it.status == ProjectStatus.PLANNING }
                        .forEach { project ->
                            val contractValue = project.contractValuePaise ?: 0L
                            val received = transactions
                                .filter { it.projectId == project.id && it.type.name == "INCOME" && !it.isDeleted }
                                .sumOf { it.amountPaise }
                            val pending = contractValue - received

                            if (pending > 0 && project.expectedCompletionDate != null) {
                                val daysUntilDue = ((project.expectedCompletionDate!! - now) / 86_400_000L).toInt()
                                val isOverdue = daysUntilDue < 0

                                val subtitle = when {
                                    isOverdue -> "Overdue by ${-daysUntilDue} days • ${CurrencyUtils.formatPaise(pending)} pending"
                                    daysUntilDue <= 7 -> "Due in $daysUntilDue days • ${CurrencyUtils.formatPaise(pending)} pending"
                                    daysUntilDue <= 30 -> "Due in ${daysUntilDue / 7} weeks • ${CurrencyUtils.formatPaise(pending)} pending"
                                    else -> "Due in ${daysUntilDue / 30} months • ${CurrencyUtils.formatPaise(pending)} pending"
                                }

                                // Only show if due within 90 days or overdue
                                if (daysUntilDue < 90) {
                                    reminders.add(
                                        PaymentReminderItem(
                                            id = project.id,
                                            title = "Payment - ${project.name}",
                                            subtitle = subtitle,
                                            dueDate = project.expectedCompletionDate!!,
                                            isOverdue = isOverdue,
                                            projectId = project.id,
                                            amountPaise = pending
                                        )
                                    )
                                }
                            }
                        }

                    val sorted = reminders.sortedWith(
                        compareByDescending<PaymentReminderItem> { it.isOverdue }
                            .thenBy { it.dueDate }
                    )

                    PaymentRemindersUiState(
                        isLoading = false,
                        reminders = sorted,
                        overdueCount = sorted.count { it.isOverdue },
                        upcomingCount = sorted.count { !it.isOverdue },
                        totalPendingPaise = sorted.sumOf { it.amountPaise }
                    )
                }
                .collect { _uiState.value = it }
        }
    }
}

class PaymentRemindersViewModelFactory(
    private val projectRepository: ProjectRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentRemindersViewModel(projectRepository, transactionRepository) as T
    }
}
