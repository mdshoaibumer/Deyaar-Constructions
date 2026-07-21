#!/bin/bash
cat << 'INNER' > patch.txt
import com.example.domain.usecase.finance.*
import com.example.data.repository.TransactionRepositoryImpl
import com.example.domain.repository.TransactionRepository

// ... inside class ...
    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(database.transactionDao())
    }
    val getTransactionsForProjectUseCase: GetTransactionsForProjectUseCase by lazy {
        GetTransactionsForProjectUseCase(transactionRepository)
    }
    val getTransactionByIdUseCase: GetTransactionByIdUseCase by lazy {
        GetTransactionByIdUseCase(transactionRepository)
    }
    val saveTransactionUseCase: SaveTransactionUseCase by lazy {
        SaveTransactionUseCase(transactionRepository)
    }
    val deleteTransactionUseCase: DeleteTransactionUseCase by lazy {
        DeleteTransactionUseCase(transactionRepository)
    }
INNER
