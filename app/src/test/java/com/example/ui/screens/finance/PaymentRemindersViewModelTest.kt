package com.example.ui.screens.finance

import com.example.domain.model.*
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentRemindersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createProject(
        id: String,
        name: String,
        contractValuePaise: Long,
        expectedCompletionDate: Long?,
        status: ProjectStatus = ProjectStatus.ACTIVE
    ) = Project(
        id = id, projectNumber = "DEY-001", name = name, clientId = "client_1",
        category = ProjectCategory.HOUSE, address = null, location = null,
        contractValuePaise = contractValuePaise, estimatedBudgetPaise = null,
        advanceReceivedPaise = null, expectedProfitPaise = null,
        startDate = System.currentTimeMillis() - 86400000L * 60,
        expectedCompletionDate = expectedCompletionDate,
        actualCompletionDate = null, status = status,
        priority = ProjectPriority.MEDIUM, engineerInCharge = null, notes = null,
        progress = 50, createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
    )

    private fun createTransaction(
        id: String,
        projectId: String,
        amountPaise: Long,
        type: TransactionType = TransactionType.INCOME
    ) = Transaction(
        id = id, projectId = projectId,
        date = System.currentTimeMillis(), time = System.currentTimeMillis(),
        type = type, category = TransactionCategory.CLIENT_PAYMENT,
        amountPaise = amountPaise, paymentMethod = PaymentMethod.CASH,
        referenceNumber = null, description = null, createdBy = "Alex",
        isDeleted = false, createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(), attachmentPath = null
    )

    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        val mockProjectRepo = object : ProjectRepository {
            override fun getAllProjects() = flowOf(emptyList<Project>())
            override fun getProjectsForClient(clientId: String) = flowOf(emptyList<Project>())
            override suspend fun getProjectById(id: String) = null
            override suspend fun saveProject(project: Project) {}
            override suspend fun deleteProject(id: String) {}
            override fun getMilestonesForProject(projectId: String) = flowOf(emptyList<Milestone>())
            override suspend fun saveMilestone(milestone: Milestone) {}
            override suspend fun saveMilestones(milestones: List<Milestone>) {}
            override fun getTimelineEventsForProject(projectId: String) = flowOf(emptyList<ProjectTimelineEvent>())
            override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {}
        }
        val mockTransactionRepo = object : TransactionRepository {
            override fun getAllTransactions() = flowOf(emptyList<Transaction>())
            override fun getTransactionsForProject(projectId: String) = flowOf(emptyList<Transaction>())
            override suspend fun getTransactionById(id: String) = null
            override suspend fun saveTransaction(transaction: Transaction) {}
            override suspend fun deleteTransaction(id: String) {}
            override fun getTotalIncomeForProject(projectId: String) = flowOf(0L)
            override fun getTotalExpenseForProject(projectId: String) = flowOf(0L)
            override fun getGlobalTotalExpenses() = flowOf(0L)
            override fun getGlobalTotalIncome() = flowOf(0L)
        }

        val viewModel = PaymentRemindersViewModel(mockProjectRepo, mockTransactionRepo)

        // Initially loading
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `no reminders when all payments received`() = runTest(testDispatcher) {
        val now = System.currentTimeMillis()
        val projects = listOf(
            createProject("proj_1", "Villa Project", 5000000L, now + 86400000L * 30)
        )
        val transactions = listOf(
            createTransaction("txn_1", "proj_1", 5000000L) // Full payment received
        )

        val mockProjectRepo = object : ProjectRepository {
            override fun getAllProjects() = flowOf(projects)
            override fun getProjectsForClient(clientId: String) = flowOf(emptyList<Project>())
            override suspend fun getProjectById(id: String) = null
            override suspend fun saveProject(project: Project) {}
            override suspend fun deleteProject(id: String) {}
            override fun getMilestonesForProject(projectId: String) = flowOf(emptyList<Milestone>())
            override suspend fun saveMilestone(milestone: Milestone) {}
            override suspend fun saveMilestones(milestones: List<Milestone>) {}
            override fun getTimelineEventsForProject(projectId: String) = flowOf(emptyList<ProjectTimelineEvent>())
            override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {}
        }
        val mockTransactionRepo = object : TransactionRepository {
            override fun getAllTransactions() = flowOf(transactions)
            override fun getTransactionsForProject(projectId: String) = flowOf(transactions)
            override suspend fun getTransactionById(id: String) = null
            override suspend fun saveTransaction(transaction: Transaction) {}
            override suspend fun deleteTransaction(id: String) {}
            override fun getTotalIncomeForProject(projectId: String) = flowOf(0L)
            override fun getTotalExpenseForProject(projectId: String) = flowOf(0L)
            override fun getGlobalTotalExpenses() = flowOf(0L)
            override fun getGlobalTotalIncome() = flowOf(0L)
        }

        val viewModel = PaymentRemindersViewModel(mockProjectRepo, mockTransactionRepo)

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(0, viewModel.uiState.value.reminders.size)

        collectJob.cancel()
    }

    @Test
    fun `shows overdue when deadline passed with pending payment`() = runTest(testDispatcher) {
        val now = System.currentTimeMillis()
        val projects = listOf(
            createProject("proj_1", "Overdue Villa", 5000000L, now - 86400000L * 5) // 5 days overdue
        )
        val transactions = listOf(
            createTransaction("txn_1", "proj_1", 2000000L) // Partial payment
        )

        val mockProjectRepo = object : ProjectRepository {
            override fun getAllProjects() = flowOf(projects)
            override fun getProjectsForClient(clientId: String) = flowOf(emptyList<Project>())
            override suspend fun getProjectById(id: String) = null
            override suspend fun saveProject(project: Project) {}
            override suspend fun deleteProject(id: String) {}
            override fun getMilestonesForProject(projectId: String) = flowOf(emptyList<Milestone>())
            override suspend fun saveMilestone(milestone: Milestone) {}
            override suspend fun saveMilestones(milestones: List<Milestone>) {}
            override fun getTimelineEventsForProject(projectId: String) = flowOf(emptyList<ProjectTimelineEvent>())
            override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {}
        }
        val mockTransactionRepo = object : TransactionRepository {
            override fun getAllTransactions() = flowOf(transactions)
            override fun getTransactionsForProject(projectId: String) = flowOf(transactions)
            override suspend fun getTransactionById(id: String) = null
            override suspend fun saveTransaction(transaction: Transaction) {}
            override suspend fun deleteTransaction(id: String) {}
            override fun getTotalIncomeForProject(projectId: String) = flowOf(0L)
            override fun getTotalExpenseForProject(projectId: String) = flowOf(0L)
            override fun getGlobalTotalExpenses() = flowOf(0L)
            override fun getGlobalTotalIncome() = flowOf(0L)
        }

        val viewModel = PaymentRemindersViewModel(mockProjectRepo, mockTransactionRepo)

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.reminders.size)
        assertTrue(state.reminders[0].isOverdue)
        assertEquals(1, state.overdueCount)
        assertEquals(3000000L, state.totalPendingPaise) // 5000000 - 2000000

        collectJob.cancel()
    }
}
