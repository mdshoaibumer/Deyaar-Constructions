package com.example.ui.screens.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Project
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectStatus
import com.example.domain.usecase.project.GetProjectsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectListViewModel(
    private val getProjectsUseCase: GetProjectsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<ProjectStatus?>(null)
    private val _selectedCategory = MutableStateFlow<ProjectCategory?>(null)

    init {
        loadProjects()

        // Combine flows for filtering and searching
        combine(_projects, _searchQuery, _selectedStatus, _selectedCategory) { projects, query, status, category ->
            var filtered = projects

            if (query.isNotBlank()) {
                val q = query.lowercase()
                filtered = filtered.filter {
                    it.name.lowercase().contains(q) ||
                    it.projectNumber.lowercase().contains(q) ||
                    (it.engineerInCharge?.lowercase()?.contains(q) ?: false)
                }
            }

            if (status != null) {
                filtered = filtered.filter { it.status == status }
            }

            if (category != null) {
                filtered = filtered.filter { it.category == category }
            }

            filtered
        }.onEach { filteredProjects ->
            _uiState.update { it.copy(projects = filteredProjects, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getProjectsUseCase().collect { projects ->
                _projects.value = projects
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onStatusFilterSelected(status: ProjectStatus?) {
        _selectedStatus.value = status
    }

    fun onCategoryFilterSelected(category: ProjectCategory?) {
        _selectedCategory.value = category
    }
}

data class ProjectListUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

class ProjectListViewModelFactory(
    private val getProjectsUseCase: GetProjectsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectListViewModel(getProjectsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
