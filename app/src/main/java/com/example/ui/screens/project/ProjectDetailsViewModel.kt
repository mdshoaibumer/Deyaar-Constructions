package com.example.ui.screens.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectTimelineEvent
import com.example.domain.usecase.client.GetClientByIdUseCase
import com.example.domain.usecase.project.GetProjectByIdUseCase
import com.example.domain.usecase.project.GetProjectMilestonesUseCase
import com.example.domain.usecase.project.GetProjectTimelineUseCase
import com.example.domain.usecase.project.SaveMilestoneUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectDetailsViewModel(
    private val projectId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val getProjectMilestonesUseCase: GetProjectMilestonesUseCase,
    private val getProjectTimelineUseCase: GetProjectTimelineUseCase,
    private val saveMilestoneUseCase: SaveMilestoneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailsUiState(isLoading = true))
    val uiState: StateFlow<ProjectDetailsUiState> = _uiState.asStateFlow()

    init {
        loadProjectDetails()
        loadMilestones()
        loadTimeline()
    }

    private fun loadProjectDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val project = getProjectByIdUseCase(projectId)
            if (project != null) {
                _uiState.update { it.copy(project = project, isLoading = false) }
                
                if (project.clientId != null) {
                    val client = getClientByIdUseCase(project.clientId!!)
                    _uiState.update { it.copy(clientName = client?.name ?: "Unknown Client") }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Project not found") }
            }
        }
    }

    private fun loadMilestones() {
        viewModelScope.launch {
            getProjectMilestonesUseCase(projectId).collect { milestones ->
                _uiState.update { it.copy(milestones = milestones) }
            }
        }
    }

    private fun loadTimeline() {
        viewModelScope.launch {
            getProjectTimelineUseCase(projectId).collect { timeline ->
                _uiState.update { it.copy(timelineEvents = timeline) }
            }
        }
    }
    
    fun toggleMilestone(milestone: Milestone) {
        viewModelScope.launch {
            val updated = milestone.copy(
                isCompleted = !milestone.isCompleted,
                completionDate = if (!milestone.isCompleted) System.currentTimeMillis() else null
            )
            saveMilestoneUseCase(updated)
        }
    }
    
    fun addMilestone(name: String) {
        viewModelScope.launch {
            val currentMilestones = _uiState.value.milestones
            val maxOrder = currentMilestones.maxOfOrNull { it.orderIndex } ?: 0
            val newMilestone = Milestone(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                name = name,
                isCompleted = false,
                completionDate = null,
                notes = null,
                orderIndex = maxOrder + 1
            )
            saveMilestoneUseCase(newMilestone)
        }
    }
}

data class ProjectDetailsUiState(
    val project: Project? = null,
    val clientName: String = "",
    val milestones: List<Milestone> = emptyList(),
    val timelineEvents: List<ProjectTimelineEvent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProjectDetailsViewModelFactory(
    private val projectId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val getProjectMilestonesUseCase: GetProjectMilestonesUseCase,
    private val getProjectTimelineUseCase: GetProjectTimelineUseCase,
    private val saveMilestoneUseCase: SaveMilestoneUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectDetailsViewModel(
                projectId,
                getProjectByIdUseCase,
                getClientByIdUseCase,
                getProjectMilestonesUseCase,
                getProjectTimelineUseCase,
                saveMilestoneUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
