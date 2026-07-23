package com.example.ui.screens.reports

import com.example.domain.model.*
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.ResourceRepository
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
class ReportsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has material usage selected`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testScheduler.advanceUntilIdle()

        assertEquals(ReportType.MATERIAL_USAGE, viewModel.uiState.value.selectedReportType)
        assertFalse(viewModel.uiState.value.isLoading)
        collectJob.cancel()
    }

    @Test
    fun `selecting report type updates state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testScheduler.advanceUntilIdle()

        viewModel.onEvent(ReportsEvent.SelectReportType(ReportType.PROFIT_LOSS))
        assertEquals(ReportType.PROFIT_LOSS, viewModel.uiState.value.selectedReportType)
        assertNull(viewModel.uiState.value.reportData)

        collectJob.cancel()
    }

    @Test
    fun `selecting project updates filter`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testScheduler.advanceUntilIdle()

        viewModel.onEvent(ReportsEvent.SelectProject("proj_001", "Test Project"))
        assertEquals("proj_001", viewModel.uiState.value.filter.projectId)
        assertEquals("Test Project", viewModel.uiState.value.filter.projectName)

        collectJob.cancel()
    }

    @Test
    fun `clear report resets report data and pdf path`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testScheduler.advanceUntilIdle()

        viewModel.onEvent(ReportsEvent.ClearReport)
        assertNull(viewModel.uiState.value.reportData)
        assertNull(viewModel.uiState.value.pdfPath)

        collectJob.cancel()
    }

    @Test
    fun `all report types are available`() {
        val types = ReportType.entries
        assertEquals(5, types.size)
        assertTrue(types.contains(ReportType.MATERIAL_USAGE))
        assertTrue(types.contains(ReportType.LABOUR_COST))
        assertTrue(types.contains(ReportType.PROJECT_EXPENSES))
        assertTrue(types.contains(ReportType.PROFIT_LOSS))
        assertTrue(types.contains(ReportType.DASHBOARD_SUMMARY))
    }

    private fun createViewModel(): ReportsViewModel {
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
        val resourceRepo = object : ResourceRepository {
            override fun getAllMaterials() = flowOf(emptyList<Material>())
            override fun getAllWorkers() = flowOf(emptyList<Worker>())
            override fun getAllSuppliers() = flowOf(emptyList<Supplier>())
            override suspend fun getMaterialById(id: String): Material? = null
            override suspend fun getWorkerById(id: String): Worker? = null
            override suspend fun getSupplierById(id: String): Supplier? = null
            override suspend fun saveMaterial(material: Material) {}
            override suspend fun saveWorker(worker: Worker) {}
            override suspend fun saveSupplier(supplier: Supplier) {}
            override suspend fun deleteMaterial(id: String) {}
            override suspend fun deleteWorker(id: String) {}
            override suspend fun deleteSupplier(id: String) {}
            override suspend fun updateStock(id: String, quantityChange: Double) {}
            override fun getAttendanceForWorker(workerId: String) = flowOf(emptyList<Attendance>())
            override fun getAttendanceForDate(date: Long) = flowOf(emptyList<Attendance>())
            override suspend fun saveAttendance(attendance: Attendance) {}
            override suspend fun saveAllAttendance(records: List<Attendance>) {}
            override suspend fun deleteAttendance(id: String) {}
            override fun getAllocationsForProject(projectId: String) = flowOf(emptyList<ResourceAllocation>())
            override fun getAllocationsForResource(resourceId: String) = flowOf(emptyList<ResourceAllocation>())
            override suspend fun saveAllocation(allocation: ResourceAllocation) {}
            override suspend fun deleteAllocation(id: String) {}
        }

        return ReportsViewModel(transactionRepo, projectRepo, resourceRepo)
    }
}
