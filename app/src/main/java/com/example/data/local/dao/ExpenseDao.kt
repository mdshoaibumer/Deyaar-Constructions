package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE projectId = :projectId ORDER BY date DESC")
    fun getExpensesForProject(projectId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE projectId = :projectId")
    fun getTotalExpensesForProject(projectId: String): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses")
    fun getTotalExpenses(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String)
}
