package com.example.ui.screens.finance

import com.example.domain.model.*
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTransaction(
        type: TransactionType = TransactionType.INCOME,
        amountPaise: Long = 100000L,
        category: TransactionCategory = TransactionCategory.CLIENT_PAYMENT
    ) = Transaction(
        id = "txn_${System.nanoTime()}",
        projectId = "proj_001",
        date = System.currentTimeMillis(),
        time = System.currentTimeMillis(),
        type = type,
        category = category,
        amountPaise = amountPaise,
        paymentMethod = PaymentMethod.UPI,
        referenceNumber = null,
        description = "Test transaction",
        createdBy = "Alex",
        isDeleted = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        attachmentPath = null
    )

    private fun createProject(contractValuePaise: Long = 5000000L) = Project(
        id = "proj_001",
        projectNumber = "DEY-001",
        name = "Test Project",
        clientId = null,
        category = ProjectCategory.HOUSE,
        address = null,
        location = null,
        contractValuePaise = contractValuePaise,
        estimatedBudgetPaise = null,
        advanceReceivedPaise = null,
        expectedProfitPaise = null,
        startDate = null,
        expectedCompletionDate = null,
        actualCompletionDate = null,
        status = ProjectStatus.ACTIVE,
        priority = ProjectPriority.MEDIUM,
        engineerInCharge = null,
        notes = null,
        progress = 50,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        val transactionRepo = object : TransactionRepository {
            override fun getAllTransactions() = flowOf(emptyList<Transaction>())
            override fun getTransactionsForProject(projectId: String) = flowOf(emptyList<Transaction>())
            override suspend fun getTransactionById(id: String) = null
            override suspend fun saveTransaction(transaction: Transaction) {}
            override suspend fun deleteTransaction(id: String) {}
            override fun getTotalIncomeForProject(projectId: String) = flowOf(0L)
            override fun getTotalExpenseForProject(projectId: String) = flowOf(0L)
            override fun getGlobalTotalIncome() = flowOf(0L)
            override fun getGlobalTotalExpenses() = flowOf(0L)
        }
        val projectRepo = object : ProjectRepository {
            override fun getAllProjects() = flowOf(emptyList<Project>())
            override suspend fun getProjectById(id: String) = null
            override suspend fun saveProject(project: Project) {}
            override suspend fun deleteProject(id: String) {}
            override fun getProjectsForClient(clientId: String) = flowOf(emptyList<Project>())
            override fun getMilestonesForProject(projectId: String) = flowOf(emptyList<Milestone>())
            override suspend fun saveMilestone(milestone: Milestone) {}
            override suspend fun saveMilestones(milestones: List<Milestone>) {}
            override fun getTimelineEventsForProject(projectId: String) = flowOf(emptyList<ProjectTimelineEvent>())
            override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {}
        }

        val viewModel = PaymentDashboardViewModel(transactionRepo, projectRepo)
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `state populates with transaction data`() = runTest(testDispatcher) {
        val income1 = createTransaction(TransactionType.INCOME, 300000L)
        val income2 = createTransaction(TransactionType.INCOME, 200000L)
        val expense1 = createTransaction(TransactionType.EXPENSE, 100000L, TransactionCategory.MATERIAL_PURCHASE)

        val transactionRepo = object : TransactionRepository {
            override fun getAllTransactions() = flowOf(listOf(income1, income2, expense1))
            override fun getTransactionsForProject(projectId: String) = flowOf(emptyList<Transaction>())
            override suspend fun getTransactionById(id: String) = null
            override suspend fun saveTransaction(transaction: Transaction) {}
            override suspend fun deleteTransaction(id: String) {}
            override fun getTotalIncomeForProject(projectId: String) = flowOf(0L)
            override fun getTotalExpenseForProject(projectId: String) = flowOf(0L)
            override fun getGlobalTotalIncome() = flowOf(500000L)
            override fun getGlobalTotalExpenses() = flowOf(100000L)
        }
        val projectRepo = object : ProjectRepository {
            override fun getAllProjects() = flowOf(listOf(createProject(1000000L)))
            override suspend fun getProjectById(id: String) = null
            override suspend fun saveProject(project: Project) {}
            override suspend fun deleteProject(id: String) {}
            override fun getProjectsForClient(clientId: String) = flowOf(emptyList<Project>())
            override fun getMilestonesForProject(projectId: String) = flowOf(emptyList<Milestone>())
            override suspend fun saveMilestone(milestone: Milestone) {}
            override suspend fun saveMilestones(milestones: List<Milestone>) {}
            override fun getTimelineEventsForProject(projectId: String) = flowOf(emptyList<ProjectTimelineEvent>())
            override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {}
        }

        val viewModel = PaymentDashboardViewModel(transactionRepo, projectRepo)

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(500000L, state.totalReceivedPaise)
        assertEquals(100000L, state.totalExpensesPaise)
        assertEquals(500000L, state.pendingPaymentsPaise) // 1000000 - 500000
        assertEquals(50, state.completionPercent) // 500000 / 1000000 * 100
        assertEquals(3, state.recentTransactions.size)

        collectJob.cancel()
    }
}
