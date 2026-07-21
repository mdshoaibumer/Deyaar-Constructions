package com.example.domain.usecase.finance

import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetGlobalFinancialStatsUseCase(
    private val repository: TransactionRepository
) {
    fun getTotalIncome(): Flow<Long> = repository.getGlobalTotalIncome()
    fun getTotalExpenses(): Flow<Long> = repository.getGlobalTotalExpenses()
}
