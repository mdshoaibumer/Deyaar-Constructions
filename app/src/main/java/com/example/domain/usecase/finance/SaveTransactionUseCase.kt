package com.example.domain.usecase.finance

import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository

class SaveTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.saveTransaction(transaction)
    }
}
