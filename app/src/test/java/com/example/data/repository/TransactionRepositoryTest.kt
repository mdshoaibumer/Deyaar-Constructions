package com.example.data.repository

import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.TransactionEntity
import com.example.domain.model.PaymentMethod
import com.example.domain.model.TransactionCategory
import com.example.domain.model.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryTest {

    private val sampleEntity = TransactionEntity(
        id = "txn_001",
        projectId = "proj_001",
        date = 1700000000000L,
        time = 1700000000000L,
        type = "INCOME",
        category = "CLIENT_PAYMENT",
        amountPaise = 150000L,
        paymentMethod = "UPI",
        referenceNumber = "REF123",
        description = "Client payment received",
        createdBy = "Alex",
        isDeleted = false,
        createdAt = 1700000000000L,
        updatedAt = 1700000000000L,
        attachmentPath = null
    )

    private val mockDao = object : TransactionDao {
        override fun getAllTransactions() = flowOf(listOf(sampleEntity))
        override fun getTransactionsForProject(projectId: String) = flowOf(
            if (projectId == "proj_001") listOf(sampleEntity) else emptyList()
        )
        override suspend fun getTransactionById(id: String) = if (id == "txn_001") sampleEntity else null
        override suspend fun insertTransaction(transaction: TransactionEntity) {}
        override suspend fun insertTransactions(transactions: List<TransactionEntity>) {}
        override suspend fun softDeleteTransaction(id: String, updatedAt: Long) {}
        override fun getTotalIncomeForProject(projectId: String) = flowOf(150000L)
        override fun getTotalExpenseForProject(projectId: String) = flowOf(50000L)
        override fun getAdvanceReceivedForProject(projectId: String) = flowOf(100000L)
        override fun getGlobalTotalExpenses() = flowOf(500000L)
        override fun getGlobalTotalIncome() = flowOf(800000L)
        override fun getGlobalTotalReceived() = flowOf(700000L)
        override fun getPaymentsForWorker(workerId: String) = flowOf(emptyList<TransactionEntity>())
        override suspend fun getExpensesForPeriod(startDate: Long, endDate: Long) = 100000L
        override fun getRecentExpenses(limit: Int) = flowOf(listOf(sampleEntity))
    }

    private val repository = TransactionRepositoryImpl(mockDao)

    @Test
    fun `getAllTransactions maps entity to domain correctly`() = runTest {
        val transactions = repository.getAllTransactions().first()

        assertEquals(1, transactions.size)
        val txn = transactions[0]
        assertEquals("txn_001", txn.id)
        assertEquals("proj_001", txn.projectId)
        assertEquals(TransactionType.INCOME, txn.type)
        assertEquals(TransactionCategory.CLIENT_PAYMENT, txn.category)
        assertEquals(150000L, txn.amountPaise)
        assertEquals(PaymentMethod.UPI, txn.paymentMethod)
        assertEquals("REF123", txn.referenceNumber)
        assertEquals("Client payment received", txn.description)
        assertEquals(false, txn.isDeleted)
    }

    @Test
    fun `getTransactionsForProject returns filtered list`() = runTest {
        val transactions = repository.getTransactionsForProject("proj_001").first()
        assertEquals(1, transactions.size)

        val empty = repository.getTransactionsForProject("proj_999").first()
        assertEquals(0, empty.size)
    }

    @Test
    fun `getTransactionById returns null for non-existent`() = runTest {
        val txn = repository.getTransactionById("txn_001")
        assertNotNull(txn)
        assertEquals("txn_001", txn!!.id)

        val notFound = repository.getTransactionById("txn_999")
        assertNull(notFound)
    }

    @Test
    fun `getTotalIncomeForProject returns correct value`() = runTest {
        val income = repository.getTotalIncomeForProject("proj_001").first()
        assertEquals(150000L, income)
    }

    @Test
    fun `getTotalExpenseForProject returns correct value`() = runTest {
        val expense = repository.getTotalExpenseForProject("proj_001").first()
        assertEquals(50000L, expense)
    }

    @Test
    fun `getGlobalTotalIncome returns non-null`() = runTest {
        val income = repository.getGlobalTotalIncome().first()
        assertEquals(800000L, income)
    }

    @Test
    fun `getGlobalTotalExpenses returns non-null`() = runTest {
        val expenses = repository.getGlobalTotalExpenses().first()
        assertEquals(500000L, expenses)
    }
}
