package com.example.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Project
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionCategory
import com.example.domain.model.TransactionType
import com.example.domain.usecase.finance.GetTransactionsForProjectUseCase
import com.example.domain.usecase.project.GetProjectByIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class FinanceLedgerViewModel(
    private val projectId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getTransactionsForProjectUseCase: GetTransactionsForProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceLedgerUiState(isLoading = true))
    val uiState: StateFlow<FinanceLedgerUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val project = getProjectByIdUseCase(projectId)
            
            getTransactionsForProjectUseCase(projectId).collect { transactions ->
                val analysis = calculateAnalysis(transactions, project)
                
                _uiState.update { 
                    it.copy(
                        project = project,
                        transactions = transactions,
                        filteredTransactions = transactions,
                        analysis = analysis,
                        isLoading = false
                    ) 
                }
            }
        }
    }

    /**
     * All financial calculations use Long (paise) arithmetic to avoid
     * floating-point precision issues.
     */
    private fun calculateAnalysis(transactions: List<Transaction>, project: Project?): FinanceAnalysisData {
        var todayIncome = 0L
        var todayExpense = 0L
        var monthIncome = 0L
        var monthExpense = 0L
        
        var totalIncome = 0L
        var totalExpense = 0L
        
        var materialCost = 0L
        var labourCost = 0L
        var otherExpenses = 0L

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_YEAR)

        for (t in transactions) {
            val tCal = Calendar.getInstance().apply { timeInMillis = t.date }
            val isToday = tCal.get(Calendar.YEAR) == currentYear && tCal.get(Calendar.DAY_OF_YEAR) == currentDay
            val isThisMonth = tCal.get(Calendar.YEAR) == currentYear && tCal.get(Calendar.MONTH) == currentMonth

            if (t.type == TransactionType.INCOME) {
                totalIncome += t.amountPaise
                if (isToday) todayIncome += t.amountPaise
                if (isThisMonth) monthIncome += t.amountPaise
            } else if (t.type == TransactionType.EXPENSE) {
                totalExpense += t.amountPaise
                if (isToday) todayExpense += t.amountPaise
                if (isThisMonth) monthExpense += t.amountPaise
                
                when (t.category) {
                    TransactionCategory.MATERIAL_PURCHASE -> materialCost += t.amountPaise
                    TransactionCategory.LABOUR_PAYMENT -> labourCost += t.amountPaise
                    else -> otherExpenses += t.amountPaise
                }
            }
        }

        val netProfit = totalIncome - totalExpense
        val contractValuePaise = project?.contractValuePaise ?: 0L
        
        val outstandingReceivables = if (contractValuePaise > 0L) {
            maxOf(0L, contractValuePaise - totalIncome)
        } else {
            0L
        }

        val estimatedProfit = if (contractValuePaise > 0L) {
            contractValuePaise - totalExpense
        } else null
        
        // Profit margin is a percentage - computed as Double for display only
        val profitMargin = if (totalIncome > 0L) {
            (netProfit.toDouble() / totalIncome.toDouble()) * 100.0
        } else null

        return FinanceAnalysisData(
            todayIncomePaise = todayIncome,
            todayExpensePaise = todayExpense,
            monthIncomePaise = monthIncome,
            monthExpensePaise = monthExpense,
            netProfitPaise = netProfit,
            outstandingReceivablesPaise = outstandingReceivables,
            outstandingPayablesPaise = 0L, // Future feature
            materialCostPaise = materialCost,
            labourCostPaise = labourCost,
            otherExpensesPaise = otherExpenses,
            estimatedProfitPaise = estimatedProfit,
            profitMarginPercent = profitMargin
        )
    }
}

/**
 * Financial analysis data. All monetary fields are in paise (1 INR = 100 paise).
 * profitMarginPercent is a percentage (e.g. 25.5 = 25.5%) - the only Double field.
 */
data class FinanceAnalysisData(
    val todayIncomePaise: Long,
    val todayExpensePaise: Long,
    val monthIncomePaise: Long,
    val monthExpensePaise: Long,
    val netProfitPaise: Long,
    val outstandingReceivablesPaise: Long,
    val outstandingPayablesPaise: Long,
    val materialCostPaise: Long,
    val labourCostPaise: Long,
    val otherExpensesPaise: Long,
    val estimatedProfitPaise: Long?,
    val profitMarginPercent: Double?
)

data class FinanceLedgerUiState(
    val project: Project? = null,
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val analysis: FinanceAnalysisData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class FinanceLedgerViewModelFactory(
    private val projectId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getTransactionsForProjectUseCase: GetTransactionsForProjectUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceLedgerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceLedgerViewModel(projectId, getProjectByIdUseCase, getTransactionsForProjectUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
