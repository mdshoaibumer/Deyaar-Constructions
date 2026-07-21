package com.example.domain.repository

import com.example.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsForProject(projectId: String): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun saveTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
    fun getTotalIncomeForProject(projectId: String): Flow<Long>
    fun getTotalExpenseForProject(projectId: String): Flow<Long>
    fun getGlobalTotalIncome(): Flow<Long>
    fun getGlobalTotalExpenses(): Flow<Long>
}
