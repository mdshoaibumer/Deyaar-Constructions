package com.example.ui.screens.project

import com.example.domain.model.Client
import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus
import com.example.domain.model.ProjectTimelineEvent
import com.example.domain.repository.ClientRepository
import com.example.domain.repository.ProjectRepository
import com.example.domain.usecase.client.GetClientsUseCase
import com.example.domain.usecase.project.GetProjectByIdUseCase
import com.example.domain.usecase.project.SaveMilestonesUseCase
import com.example.domain.usecase.project.SaveProjectTimelineEventUseCase
import com.example.domain.usecase.project.SaveProjectUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectAddEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var saveProjectUseCase: SaveProjectUseCase
    private lateinit var saveProjectTimelineEventUseCase: SaveProjectTimelineEventUseCase
    private lateinit var saveMilestonesUseCase: SaveMilestonesUseCase
    private lateinit var getClientsUseCase: GetClientsUseCase
    
    private val savedProjects = mutableListOf<Project>()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedProjects.clear()
        
        val fakeProjectRepository = object : ProjectRepository {
            override fun getAllProjects(): Flow<List<Project>> = flowOf(savedProjects)
            override fun getProjectsForClient(clientId: String): Flow<List<Project>> = flowOf(emptyList())
            override suspend fun getProjectById(id: String): Project? = savedProjects.find { it.id == id }
            override suspend fun saveProject(project: Project) { savedProjects.add(project) }
            override suspend fun deleteProject(id: String) {}
            override fun getMilestonesForProject(projectId: String): Flow<List<Milestone>> = flowOf(emptyList())
            override suspend fun saveMilestone(milestone: Milestone) {}
            override suspend fun saveMilestones(milestones: List<Milestone>) {}
            override fun getTimelineEventsForProject(projectId: String): Flow<List<ProjectTimelineEvent>> = flowOf(emptyList())
            override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {}
        }
        
        val fakeClientRepository = object : ClientRepository {
            override fun getAllClients(): Flow<List<Client>> = flowOf(emptyList())
            override suspend fun getClientById(id: String): Client? = null
            override suspend fun saveClient(client: Client) {}
            override suspend fun deleteClient(id: String) {}
        }
        
        getProjectByIdUseCase = GetProjectByIdUseCase(fakeProjectRepository)
        saveProjectUseCase = SaveProjectUseCase(fakeProjectRepository)
        saveProjectTimelineEventUseCase = SaveProjectTimelineEventUseCase(fakeProjectRepository)
        saveMilestonesUseCase = SaveMilestonesUseCase(fakeProjectRepository)
        getClientsUseCase = GetClientsUseCase(fakeClientRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save with valid data updates isSaved state`() = runTest(testDispatcher) {
        val viewModel = ProjectAddEditViewModel(
            null,
            getProjectByIdUseCase,
            saveProjectUseCase,
            saveProjectTimelineEventUseCase,
            saveMilestonesUseCase,
            getClientsUseCase
        )

        viewModel.onEvent(ProjectAddEditEvent.NameChanged("Test Project"))
        viewModel.onEvent(ProjectAddEditEvent.ClientSelected("client1"))
        
        viewModel.onEvent(ProjectAddEditEvent.Save)
        
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
        assertEquals(1, savedProjects.size)
    }
}
