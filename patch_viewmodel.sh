#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/ui/screens/finance/FinanceLedgerViewModel.kt
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

    private fun calculateAnalysis(transactions: List<Transaction>, project: Project?): FinanceAnalysisData {
        var todayIncome = 0.0
        var todayExpense = 0.0
        var monthIncome = 0.0
        var monthExpense = 0.0
        
        var totalIncome = 0.0
        var totalExpense = 0.0
        
        var materialCost = 0.0
        var labourCost = 0.0
        var otherExpenses = 0.0

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_YEAR)

        for (t in transactions) {
            val tCal = Calendar.getInstance().apply { timeInMillis = t.date }
            val isToday = tCal.get(Calendar.YEAR) == currentYear && tCal.get(Calendar.DAY_OF_YEAR) == currentDay
            val isThisMonth = tCal.get(Calendar.YEAR) == currentYear && tCal.get(Calendar.MONTH) == currentMonth

            if (t.type == TransactionType.INCOME) {
                totalIncome += t.amount
                if (isToday) todayIncome += t.amount
                if (isThisMonth) monthIncome += t.amount
            } else if (t.type == TransactionType.EXPENSE) {
                totalExpense += t.amount
                if (isToday) todayExpense += t.amount
                if (isThisMonth) monthExpense += t.amount
                
                when (t.category) {
                    TransactionCategory.MATERIAL_PURCHASE -> materialCost += t.amount
                    TransactionCategory.LABOUR_PAYMENT -> labourCost += t.amount
                    else -> otherExpenses += t.amount
                }
            }
        }

        val netProfit = totalIncome - totalExpense
        val contractValue = project?.contractValue ?: 0.0
        
        val outstandingReceivables = if (contractValue > 0) {
            maxOf(0.0, contractValue - totalIncome)
        } else {
            0.0
        }

        val estimatedProfit = if (contractValue > 0) {
            contractValue - totalExpense
        } else null
        
        val profitMargin = if (totalIncome > 0) {
            (netProfit / totalIncome) * 100
        } else null

        return FinanceAnalysisData(
            todayIncome = todayIncome,
            todayExpense = todayExpense,
            monthIncome = monthIncome,
            monthExpense = monthExpense,
            netProfit = netProfit,
            outstandingReceivables = outstandingReceivables,
            outstandingPayables = 0.0, // Future feature
            materialCost = materialCost,
            labourCost = labourCost,
            otherExpenses = otherExpenses,
            estimatedProfit = estimatedProfit,
            profitMargin = profitMargin
        )
    }
}

data class FinanceAnalysisData(
    val todayIncome: Double,
    val todayExpense: Double,
    val monthIncome: Double,
    val monthExpense: Double,
    val netProfit: Double,
    val outstandingReceivables: Double,
    val outstandingPayables: Double,
    val materialCost: Double,
    val labourCost: Double,
    val otherExpenses: Double,
    val estimatedProfit: Double?,
    val profitMargin: Double?
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
INNER
