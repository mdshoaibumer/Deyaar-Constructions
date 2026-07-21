package com.example.domain.usecase.finance

import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsForProjectUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(projectId: String): Flow<List<Transaction>> {
        return repository.getTransactionsForProject(projectId)
    }
}
