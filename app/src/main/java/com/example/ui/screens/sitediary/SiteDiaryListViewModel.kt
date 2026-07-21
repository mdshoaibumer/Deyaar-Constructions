package com.example.ui.screens.sitediary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Project
import com.example.domain.model.SiteDiary
import com.example.domain.usecase.project.GetProjectByIdUseCase
import com.example.domain.usecase.sitediary.GetSiteDiariesForProjectUseCase
import com.example.domain.usecase.sitediary.GetOrCreateSiteDiaryForDateUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SiteDiaryListViewModel(
    private val projectId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getSiteDiariesForProjectUseCase: GetSiteDiariesForProjectUseCase,
    private val getOrCreateSiteDiaryForDateUseCase: GetOrCreateSiteDiaryForDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SiteDiaryListUiState(isLoading = true))
    val uiState: StateFlow<SiteDiaryListUiState> = _uiState.asStateFlow()

    init {
        loadProjectAndDiaries()
    }

    private fun loadProjectAndDiaries() {
        viewModelScope.launch {
            val project = getProjectByIdUseCase(projectId)
            if (project != null) {
                _uiState.update { it.copy(project = project) }
                
                getSiteDiariesForProjectUseCase(projectId).collect { diaries ->
                    _uiState.update { it.copy(diaries = diaries, isLoading = false) }
                }
            } else {
                _uiState.update { it.copy(error = "Project not found", isLoading = false) }
            }
        }
    }
    
    fun createTodayDiary(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val today = System.currentTimeMillis()
            val diary = getOrCreateSiteDiaryForDateUseCase(projectId, today)
            onSuccess(diary.id)
        }
    }
}

data class SiteDiaryListUiState(
    val project: Project? = null,
    val diaries: List<SiteDiary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SiteDiaryListViewModelFactory(
    private val projectId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getSiteDiariesForProjectUseCase: GetSiteDiariesForProjectUseCase,
    private val getOrCreateSiteDiaryForDateUseCase: GetOrCreateSiteDiaryForDateUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SiteDiaryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SiteDiaryListViewModel(
                projectId, 
                getProjectByIdUseCase, 
                getSiteDiariesForProjectUseCase,
                getOrCreateSiteDiaryForDateUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
