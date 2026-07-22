package com.example.ui.screens.project

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.domain.model.Client
import com.example.domain.model.Project
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus
import com.example.domain.model.ProjectTimelineEvent
import com.example.domain.model.TimelineEventType
import com.example.domain.usecase.client.GetClientsUseCase
import com.example.domain.usecase.project.GetProjectByIdUseCase
import com.example.domain.usecase.project.SaveProjectTimelineEventUseCase
import com.example.domain.usecase.project.SaveProjectUseCase
import com.example.domain.usecase.project.SaveMilestonesUseCase
import com.example.core.util.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class ProjectAddEditViewModel(
    private val projectId: String?,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val saveProjectUseCase: SaveProjectUseCase,
    private val saveProjectTimelineEventUseCase: SaveProjectTimelineEventUseCase,
    private val saveMilestonesUseCase: SaveMilestonesUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreState() ?: ProjectAddEditUiState())
    val uiState: StateFlow<ProjectAddEditUiState> = _uiState.asStateFlow()

    init {
        loadClients()
        if (projectId != null && !savedStateHandle.contains(KEY_PROJECT_NUMBER)) {
            loadProject(projectId)
        } else if (projectId == null && !savedStateHandle.contains(KEY_PROJECT_NUMBER)) {
            _uiState.update { it.copy(projectNumber = generateProjectNumber()) }
            saveState()
        }
    }

    private fun generateProjectNumber(): String {
        return "PRJ-${Random.nextInt(1000, 9999)}"
    }

    private fun loadClients() {
        viewModelScope.launch {
            getClientsUseCase().collect { clients ->
                _uiState.update { it.copy(clients = clients) }
            }
        }
    }

    private fun loadProject(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val project = getProjectByIdUseCase(id)
            if (project != null) {
                _uiState.update {
                    it.copy(
                        projectNumber = project.projectNumber,
                        name = project.name,
                        clientId = project.clientId,
                        category = project.category,
                        address = project.address ?: "",
                        contractValue = project.contractValuePaise?.let { p -> CurrencyUtils.paiseToDisplayString(p) } ?: "",
                        estimatedBudget = project.estimatedBudgetPaise?.let { p -> CurrencyUtils.paiseToDisplayString(p) } ?: "",
                        startDate = project.startDate,
                        expectedCompletionDate = project.expectedCompletionDate,
                        status = project.status,
                        priority = project.priority,
                        engineerInCharge = project.engineerInCharge ?: "",
                        notes = project.notes ?: "",
                        progress = project.progress.toString(),
                        isLoading = false
                    )
                }
                saveState()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Project not found") }
            }
        }
    }

    fun onEvent(event: ProjectAddEditEvent) {
        when (event) {
            is ProjectAddEditEvent.NameChanged -> _uiState.update { it.copy(name = event.name, error = null) }
            is ProjectAddEditEvent.ClientSelected -> _uiState.update { it.copy(clientId = event.clientId, error = null) }
            is ProjectAddEditEvent.CategoryChanged -> _uiState.update { it.copy(category = event.category) }
            is ProjectAddEditEvent.AddressChanged -> _uiState.update { it.copy(address = event.address) }
            is ProjectAddEditEvent.ContractValueChanged -> _uiState.update { it.copy(contractValue = event.value) }
            is ProjectAddEditEvent.BudgetChanged -> _uiState.update { it.copy(estimatedBudget = event.value) }
            is ProjectAddEditEvent.StartDateChanged -> _uiState.update { it.copy(startDate = event.date) }
            is ProjectAddEditEvent.ExpectedCompletionChanged -> _uiState.update { it.copy(expectedCompletionDate = event.date) }
            is ProjectAddEditEvent.StatusChanged -> _uiState.update { it.copy(status = event.status) }
            is ProjectAddEditEvent.PriorityChanged -> _uiState.update { it.copy(priority = event.priority) }
            is ProjectAddEditEvent.EngineerChanged -> _uiState.update { it.copy(engineerInCharge = event.engineer) }
            is ProjectAddEditEvent.NotesChanged -> _uiState.update { it.copy(notes = event.notes) }
            is ProjectAddEditEvent.ProgressChanged -> _uiState.update { it.copy(progress = event.progress) }
            is ProjectAddEditEvent.Save -> saveProject()
        }
        saveState()
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_PROJECT_NUMBER] = state.projectNumber
        savedStateHandle[KEY_NAME] = state.name
        savedStateHandle[KEY_CLIENT_ID] = state.clientId
        savedStateHandle[KEY_CATEGORY] = state.category.name
        savedStateHandle[KEY_ADDRESS] = state.address
        savedStateHandle[KEY_CONTRACT_VALUE] = state.contractValue
        savedStateHandle[KEY_ESTIMATED_BUDGET] = state.estimatedBudget
        savedStateHandle[KEY_START_DATE] = state.startDate
        savedStateHandle[KEY_EXPECTED_COMPLETION_DATE] = state.expectedCompletionDate
        savedStateHandle[KEY_STATUS] = state.status.name
        savedStateHandle[KEY_PRIORITY] = state.priority.name
        savedStateHandle[KEY_ENGINEER_IN_CHARGE] = state.engineerInCharge
        savedStateHandle[KEY_NOTES] = state.notes
        savedStateHandle[KEY_PROGRESS] = state.progress
    }

    private fun restoreState(): ProjectAddEditUiState? {
        val projectNumber = savedStateHandle.get<String>(KEY_PROJECT_NUMBER) ?: return null
        return ProjectAddEditUiState(
            projectNumber = projectNumber,
            name = savedStateHandle[KEY_NAME] ?: "",
            clientId = savedStateHandle.get<String>(KEY_CLIENT_ID),
            category = savedStateHandle.get<String>(KEY_CATEGORY)?.let {
                try { ProjectCategory.valueOf(it) } catch (_: Exception) { ProjectCategory.HOUSE }
            } ?: ProjectCategory.HOUSE,
            address = savedStateHandle[KEY_ADDRESS] ?: "",
            contractValue = savedStateHandle[KEY_CONTRACT_VALUE] ?: "",
            estimatedBudget = savedStateHandle[KEY_ESTIMATED_BUDGET] ?: "",
            startDate = savedStateHandle.get<Long>(KEY_START_DATE),
            expectedCompletionDate = savedStateHandle.get<Long>(KEY_EXPECTED_COMPLETION_DATE),
            status = savedStateHandle.get<String>(KEY_STATUS)?.let {
                try { ProjectStatus.valueOf(it) } catch (_: Exception) { ProjectStatus.PLANNING }
            } ?: ProjectStatus.PLANNING,
            priority = savedStateHandle.get<String>(KEY_PRIORITY)?.let {
                try { ProjectPriority.valueOf(it) } catch (_: Exception) { ProjectPriority.MEDIUM }
            } ?: ProjectPriority.MEDIUM,
            engineerInCharge = savedStateHandle[KEY_ENGINEER_IN_CHARGE] ?: "",
            notes = savedStateHandle[KEY_NOTES] ?: "",
            progress = savedStateHandle[KEY_PROGRESS] ?: "0"
        )
    }

    private fun saveProject() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Project name is required") }
            return
        }
        
        if (state.clientId == null) {
            _uiState.update { it.copy(error = "Please select a client") }
            return
        }

        viewModelScope.launch {
            try {
                val isNew = projectId == null
                val id = projectId ?: UUID.randomUUID().toString()
                val timestamp = System.currentTimeMillis()
            
                val project = Project(
                    id = id,
                    projectNumber = state.projectNumber,
                    name = state.name.trim(),
                    clientId = state.clientId,
                    category = state.category,
                    address = state.address.trim().takeIf { it.isNotBlank() },
                    location = null,
                    contractValuePaise = CurrencyUtils.displayStringToPaise(state.contractValue),
                    estimatedBudgetPaise = CurrencyUtils.displayStringToPaise(state.estimatedBudget),
                    advanceReceivedPaise = null,
                    expectedProfitPaise = null,
                    startDate = state.startDate,
                    expectedCompletionDate = state.expectedCompletionDate,
                    actualCompletionDate = if (state.status == ProjectStatus.COMPLETED) timestamp else null,
                    status = state.status,
                    priority = state.priority,
                    engineerInCharge = state.engineerInCharge.trim().takeIf { it.isNotBlank() },
                    notes = state.notes.trim().takeIf { it.isNotBlank() },
                    progress = state.progress.toIntOrNull() ?: 0,
                    createdAt = if (isNew) timestamp else timestamp,
                    updatedAt = timestamp
                )

                saveProjectUseCase(project)
            
                if (isNew) {
                    val defaultMilestones = listOf(
                        "Foundation", "Columns", "Roof", "Plastering", 
                        "Flooring", "Painting", "Electrical", "Plumbing", "Finishing"
                    )
                    val milestonesToSave = defaultMilestones.mapIndexed { index, name ->
                        com.example.domain.model.Milestone(
                            id = UUID.randomUUID().toString(),
                            projectId = id,
                            name = name,
                            isCompleted = false,
                            completionDate = null,
                            notes = null,
                            orderIndex = index
                        )
                    }
                    saveMilestonesUseCase(milestonesToSave)

                    saveProjectTimelineEventUseCase(
                        ProjectTimelineEvent(
                            id = UUID.randomUUID().toString(),
                            projectId = id,
                            title = "Project Created",
                            description = "Project ${project.projectNumber} was created.",
                            timestamp = timestamp,
                            type = TimelineEventType.CREATED
                        )
                    )
                } else {
                    saveProjectTimelineEventUseCase(
                        ProjectTimelineEvent(
                            id = UUID.randomUUID().toString(),
                            projectId = id,
                            title = "Project Updated",
                            description = "Project details were updated.",
                            timestamp = timestamp,
                            type = TimelineEventType.EDITED
                        )
                    )
                }

                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred") }
            }
        }
    }

    companion object {
        private const val KEY_PROJECT_NUMBER = "project_projectNumber"
        private const val KEY_NAME = "project_name"
        private const val KEY_CLIENT_ID = "project_clientId"
        private const val KEY_CATEGORY = "project_category"
        private const val KEY_ADDRESS = "project_address"
        private const val KEY_CONTRACT_VALUE = "project_contractValue"
        private const val KEY_ESTIMATED_BUDGET = "project_estimatedBudget"
        private const val KEY_START_DATE = "project_startDate"
        private const val KEY_EXPECTED_COMPLETION_DATE = "project_expectedCompletionDate"
        private const val KEY_STATUS = "project_status"
        private const val KEY_PRIORITY = "project_priority"
        private const val KEY_ENGINEER_IN_CHARGE = "project_engineerInCharge"
        private const val KEY_NOTES = "project_notes"
        private const val KEY_PROGRESS = "project_progress"
    }
}

data class ProjectAddEditUiState(
    val projectNumber: String = "",
    val name: String = "",
    val clientId: String? = null,
    val category: ProjectCategory = ProjectCategory.HOUSE,
    val address: String = "",
    val contractValue: String = "",
    val estimatedBudget: String = "",
    val startDate: Long? = null,
    val expectedCompletionDate: Long? = null,
    val status: ProjectStatus = ProjectStatus.PLANNING,
    val priority: ProjectPriority = ProjectPriority.MEDIUM,
    val engineerInCharge: String = "",
    val notes: String = "",
    val progress: String = "0",
    val clients: List<Client> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

sealed class ProjectAddEditEvent {
    data class NameChanged(val name: String) : ProjectAddEditEvent()
    data class ClientSelected(val clientId: String) : ProjectAddEditEvent()
    data class CategoryChanged(val category: ProjectCategory) : ProjectAddEditEvent()
    data class AddressChanged(val address: String) : ProjectAddEditEvent()
    data class ContractValueChanged(val value: String) : ProjectAddEditEvent()
    data class BudgetChanged(val value: String) : ProjectAddEditEvent()
    data class StartDateChanged(val date: Long?) : ProjectAddEditEvent()
    data class ExpectedCompletionChanged(val date: Long?) : ProjectAddEditEvent()
    data class StatusChanged(val status: ProjectStatus) : ProjectAddEditEvent()
    data class PriorityChanged(val priority: ProjectPriority) : ProjectAddEditEvent()
    data class EngineerChanged(val engineer: String) : ProjectAddEditEvent()
    data class NotesChanged(val notes: String) : ProjectAddEditEvent()
    data class ProgressChanged(val progress: String) : ProjectAddEditEvent()
    object Save : ProjectAddEditEvent()
}

class ProjectAddEditViewModelFactory(
    private val projectId: String?,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val saveProjectUseCase: SaveProjectUseCase,
    private val saveProjectTimelineEventUseCase: SaveProjectTimelineEventUseCase,
    private val saveMilestonesUseCase: SaveMilestonesUseCase,
    private val getClientsUseCase: GetClientsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ProjectAddEditViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return ProjectAddEditViewModel(
                projectId,
                getProjectByIdUseCase,
                saveProjectUseCase,
                saveProjectTimelineEventUseCase,
                saveMilestonesUseCase,
                getClientsUseCase,
                savedStateHandle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
