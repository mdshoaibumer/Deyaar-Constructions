package com.example.domain.usecase.finance

import com.example.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteTransaction(id)
    }
}
