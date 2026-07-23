package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY date DESC, time DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE projectId = :projectId AND isDeleted = 0 ORDER BY date DESC, time DESC")
    fun getTransactionsForProject(projectId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("UPDATE transactions SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteTransaction(id: String, updatedAt: Long)

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE projectId = :projectId AND type = 'INCOME' AND isDeleted = 0")
    fun getTotalIncomeForProject(projectId: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE projectId = :projectId AND type = 'EXPENSE' AND isDeleted = 0")
    fun getTotalExpenseForProject(projectId: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE projectId = :projectId AND category = 'CLIENT_ADVANCE' AND isDeleted = 0")
    fun getAdvanceReceivedForProject(projectId: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE type = 'EXPENSE' AND isDeleted = 0")
    fun getGlobalTotalExpenses(): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE type = 'INCOME' AND isDeleted = 0")
    fun getGlobalTotalIncome(): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM transactions WHERE type = 'INCOME' AND isDeleted = 0 AND category IN ('CLIENT_ADVANCE', 'CLIENT_PAYMENT')")
    fun getGlobalTotalReceived(): Flow<Long?>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND category = 'LABOUR_PAYMENT' AND description LIKE '%' || :workerId || '%' ORDER BY date DESC")
    fun getPaymentsForWorker(workerId: String): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amountPaise), 0) FROM transactions WHERE type = 'EXPENSE' AND isDeleted = 0 AND date >= :startDate AND date < :endDate")
    suspend fun getExpensesForPeriod(startDate: Long, endDate: Long): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = 'EXPENSE' ORDER BY date DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int): Flow<List<TransactionEntity>>
}
