package com.example.domain.usecase.finance

import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository

class GetTransactionByIdUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: String): Transaction? {
        return repository.getTransactionById(id)
    }
}
