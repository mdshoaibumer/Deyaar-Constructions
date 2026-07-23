package com.example.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PaymentDashboardUiState(
    val totalReceivedPaise: Long = 0L,
    val totalExpensesPaise: Long = 0L,
    val pendingPaymentsPaise: Long = 0L,
    val totalContractValuePaise: Long = 0L,
    val recentTransactions: List<Transaction> = emptyList(),
    val completionPercent: Int = 0,
    val monthlyInflows: List<Long> = emptyList(),
    val isLoading: Boolean = true
)

class PaymentDashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentDashboardUiState())
    val uiState: StateFlow<PaymentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                transactionRepository.getAllTransactions(),
                projectRepository.getAllProjects()
            ) { transactions, projects ->
                val totalReceived = transactions
                    .filter { it.type == TransactionType.INCOME && !it.isDeleted }
                    .sumOf { it.amountPaise }
                val totalExpenses = transactions
                    .filter { it.type == TransactionType.EXPENSE && !it.isDeleted }
                    .sumOf { it.amountPaise }
                val totalContractValue = projects.sumOf { it.contractValuePaise ?: 0L }
                val pending = maxOf(0L, totalContractValue - totalReceived)
                val completionPercent = if (totalContractValue > 0L) {
                    ((totalReceived.toDouble() / totalContractValue.toDouble()) * 100).toInt()
                } else 0

                val recentTxns = transactions.take(5)

                PaymentDashboardUiState(
                    totalReceivedPaise = totalReceived,
                    totalExpensesPaise = totalExpenses,
                    pendingPaymentsPaise = pending,
                    totalContractValuePaise = totalContractValue,
                    recentTransactions = recentTxns,
                    completionPercent = completionPercent,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

class PaymentDashboardViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val projectRepository: ProjectRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentDashboardViewModel(transactionRepository, projectRepository) as T
    }
}
