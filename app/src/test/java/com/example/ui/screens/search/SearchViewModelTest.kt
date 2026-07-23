package com.example.ui.screens.search

import com.example.domain.model.*
import com.example.domain.repository.ClientRepository
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.ResourceRepository
import com.example.domain.usecase.search.GlobalSearchResult
import com.example.domain.usecase.search.GlobalSearchUseCase
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeClientRepo() = object : ClientRepository {
        override fun getAllClients() = flowOf(emptyList<Client>())
        override suspend fun getClientById(id: String) = null
        override suspend fun saveClient(client: Client) {}
        override suspend fun deleteClient(id: String) {}
    }

    private fun fakeProjectRepo() = object : ProjectRepository {
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

    private fun fakeResourceRepo() = object : ResourceRepository {
        override fun getAllMaterials() = flowOf(emptyList<Material>())
        override fun getAllWorkers() = flowOf(emptyList<Worker>())
        override fun getAllSuppliers() = flowOf(emptyList<Supplier>())
        override suspend fun getMaterialById(id: String) = null
        override suspend fun getWorkerById(id: String) = null
        override suspend fun getSupplierById(id: String) = null
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
        override suspend fun saveAllAttendance(attendanceList: List<Attendance>) {}
        override suspend fun deleteAttendance(id: String) {}
        override fun getAllocationsForProject(projectId: String) = flowOf(emptyList<ResourceAllocation>())
        override fun getAllocationsForResource(resourceId: String) = flowOf(emptyList<ResourceAllocation>())
        override suspend fun saveAllocation(allocation: ResourceAllocation) {}
        override suspend fun deleteAllocation(id: String) {}
    }

    @Test
    fun `initial state has empty query`() = runTest(testDispatcher) {
        val useCase = GlobalSearchUseCase(fakeClientRepo(), fakeProjectRepo(), fakeResourceRepo())
        val viewModel = SearchViewModel(useCase)

        assertEquals("", viewModel.uiState.value.query)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `query change updates state`() = runTest(testDispatcher) {
        val useCase = GlobalSearchUseCase(fakeClientRepo(), fakeProjectRepo(), fakeResourceRepo())
        val viewModel = SearchViewModel(useCase)

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.onQueryChanged("cement")
        testScheduler.advanceTimeBy(400) // past 300ms debounce
        testScheduler.runCurrent()

        assertEquals("cement", viewModel.uiState.value.query)
        collectJob.cancel()
    }
}
