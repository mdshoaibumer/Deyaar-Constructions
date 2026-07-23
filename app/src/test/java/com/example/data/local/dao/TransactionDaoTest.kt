package com.example.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.TransactionEntity
import com.example.domain.model.ClientCategory
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransactionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var transactionDao: TransactionDao
    private lateinit var projectDao: ProjectDao
    private lateinit var clientDao: ClientDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        transactionDao = database.transactionDao()
        projectDao = database.projectDao()
        clientDao = database.clientDao()

        // Seed required parent data
        kotlinx.coroutines.runBlocking {
            clientDao.insertClient(ClientEntity(
                id = "client_1", name = "Test", phone = "9876543210",
                email = null, address = null, notes = null,
                createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
                isActive = true, category = ClientCategory.RESIDENTIAL, isFavorite = false
            ))
            projectDao.insertProject(ProjectEntity(
                id = "proj_1", projectNumber = "DEY-001", name = "Test Project",
                clientId = "client_1", category = ProjectCategory.HOUSE,
                address = null, location = null, contractValuePaise = 5000000L,
                estimatedBudgetPaise = null, advanceReceivedPaise = null,
                expectedProfitPaise = null, startDate = null, expectedCompletionDate = null,
                actualCompletionDate = null, status = ProjectStatus.ACTIVE,
                priority = ProjectPriority.MEDIUM, engineerInCharge = null, notes = null,
                progress = 0, createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
            ))
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createTransaction(
        id: String = "txn_1",
        type: String = "INCOME",
        category: String = "CLIENT_PAYMENT",
        amountPaise: Long = 100000L,
        isDeleted: Boolean = false
    ) = TransactionEntity(
        id = id, projectId = "proj_1", date = System.currentTimeMillis(),
        time = System.currentTimeMillis(), type = type, category = category,
        amountPaise = amountPaise, paymentMethod = "CASH", referenceNumber = null,
        description = "Test transaction", createdBy = "Alex", isDeleted = isDeleted,
        createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
        attachmentPath = null
    )

    @Test
    fun insertAndGetTransaction() = runTest {
        transactionDao.insertTransaction(createTransaction())

        val txn = transactionDao.getTransactionById("txn_1")
        assertNotNull(txn)
        assertEquals(100000L, txn!!.amountPaise)
        assertEquals("INCOME", txn.type)
    }

    @Test
    fun getAllTransactions_excludesDeleted() = runTest {
        transactionDao.insertTransaction(createTransaction("txn_1"))
        transactionDao.insertTransaction(createTransaction("txn_2", isDeleted = true))

        val transactions = transactionDao.getAllTransactions().first()
        assertEquals(1, transactions.size)
        assertEquals("txn_1", transactions[0].id)
    }

    @Test
    fun softDeleteTransaction_setsDeletedFlag() = runTest {
        transactionDao.insertTransaction(createTransaction())

        transactionDao.softDeleteTransaction("txn_1", System.currentTimeMillis())

        val txn = transactionDao.getTransactionById("txn_1")
        assertTrue(txn!!.isDeleted)
    }

    @Test
    fun getTotalIncomeForProject_sumsCorrectly() = runTest {
        transactionDao.insertTransaction(createTransaction("txn_1", "INCOME", amountPaise = 100000L))
        transactionDao.insertTransaction(createTransaction("txn_2", "INCOME", amountPaise = 200000L))
        transactionDao.insertTransaction(createTransaction("txn_3", "EXPENSE", amountPaise = 50000L))

        val income = transactionDao.getTotalIncomeForProject("proj_1").first()
        assertEquals(300000L, income)
    }

    @Test
    fun getTotalExpenseForProject_sumsCorrectly() = runTest {
        transactionDao.insertTransaction(createTransaction("txn_1", "EXPENSE", amountPaise = 75000L))
        transactionDao.insertTransaction(createTransaction("txn_2", "EXPENSE", amountPaise = 25000L))
        transactionDao.insertTransaction(createTransaction("txn_3", "INCOME", amountPaise = 500000L))

        val expense = transactionDao.getTotalExpenseForProject("proj_1").first()
        assertEquals(100000L, expense)
    }

    @Test
    fun getTransactionsForProject_filtersCorrectly() = runTest {
        transactionDao.insertTransaction(createTransaction("txn_1"))

        val transactions = transactionDao.getTransactionsForProject("proj_1").first()
        assertEquals(1, transactions.size)

        val empty = transactionDao.getTransactionsForProject("proj_999").first()
        assertEquals(0, empty.size)
    }

    @Test
    fun cascade_deleteProject_deletesTransactions() = runTest {
        transactionDao.insertTransaction(createTransaction())

        projectDao.deleteProject("proj_1")

        val txn = transactionDao.getTransactionById("txn_1")
        assertNull(txn) // Should be CASCADE deleted
    }

    @Test
    fun getGlobalTotalExpenses_returnsSum() = runTest {
        transactionDao.insertTransaction(createTransaction("txn_1", "EXPENSE", amountPaise = 100000L))
        transactionDao.insertTransaction(createTransaction("txn_2", "EXPENSE", amountPaise = 200000L))

        val total = transactionDao.getGlobalTotalExpenses().first()
        assertEquals(300000L, total)
    }

    @Test
    fun getGlobalTotalIncome_returnsSum() = runTest {
        transactionDao.insertTransaction(createTransaction("txn_1", "INCOME", amountPaise = 500000L))
        transactionDao.insertTransaction(createTransaction("txn_2", "INCOME", amountPaise = 300000L))

        val total = transactionDao.getGlobalTotalIncome().first()
        assertEquals(800000L, total)
    }

    @Test
    fun insertTransactions_batchInsert() = runTest {
        val transactions = (1..10).map { createTransaction("txn_$it") }
        transactionDao.insertTransactions(transactions)

        val all = transactionDao.getAllTransactions().first()
        assertEquals(10, all.size)
    }
}
