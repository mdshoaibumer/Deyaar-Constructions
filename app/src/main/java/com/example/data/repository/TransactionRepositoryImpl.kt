package com.example.data.repository

import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.TransactionEntity
import com.example.domain.model.PaymentMethod
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionCategory
import com.example.domain.model.TransactionType
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsForProject(projectId: String): Flow<List<Transaction>> {
        return dao.getTransactionsForProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id: String) {
        dao.softDeleteTransaction(id, System.currentTimeMillis())
    }

    override fun getTotalIncomeForProject(projectId: String): Flow<Long> {
        return dao.getTotalIncomeForProject(projectId).map { it ?: 0L }
    }

    override fun getTotalExpenseForProject(projectId: String): Flow<Long> {
        return dao.getTotalExpenseForProject(projectId).map { it ?: 0L }
    }

    override fun getGlobalTotalIncome(): Flow<Long> {
        return dao.getGlobalTotalIncome().map { it ?: 0L }
    }

    override fun getGlobalTotalExpenses(): Flow<Long> {
        return dao.getGlobalTotalExpenses().map { it ?: 0L }
    }

    private fun TransactionEntity.toDomain() = Transaction(
        id = id,
        projectId = projectId,
        date = date,
        time = time,
        type = TransactionType.valueOf(type),
        category = TransactionCategory.valueOf(category),
        amountPaise = amountPaise,
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        referenceNumber = referenceNumber,
        description = description,
        createdBy = createdBy,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        attachmentPath = attachmentPath
    )

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        projectId = projectId,
        date = date,
        time = time,
        type = type.name,
        category = category.name,
        amountPaise = amountPaise,
        paymentMethod = paymentMethod.name,
        referenceNumber = referenceNumber,
        description = description,
        createdBy = createdBy,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        attachmentPath = attachmentPath
    )
}
