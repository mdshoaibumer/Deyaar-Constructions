package com.example.ui.screens.sitediary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.*
import com.example.domain.usecase.project.GetProjectByIdUseCase
import com.example.domain.usecase.sitediary.GetSiteDiaryDetailsUseCase
import com.example.domain.usecase.sitediary.SaveSiteDiaryDetailsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class SiteDiaryDetailsViewModel(
    private val diaryId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getSiteDiaryDetailsUseCase: GetSiteDiaryDetailsUseCase,
    private val saveSiteDiaryDetailsUseCase: SaveSiteDiaryDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SiteDiaryDetailsUiState(isLoading = true))
    val uiState: StateFlow<SiteDiaryDetailsUiState> = _uiState.asStateFlow()

    private var detailsLoaded = false
    private var isSaving = false

    init {
        loadDiaryDetails()
    }

    private fun loadDiaryDetails() {
        viewModelScope.launch {
            getSiteDiaryDetailsUseCase(diaryId).collect { details ->
                if (details != null && !isSaving) {
                    val project = getProjectByIdUseCase(details.diary.projectId)
                    _uiState.update { state ->
                        state.copy(
                            project = project,
                            diaryDetails = details,
                            isLoading = false
                        )
                    }
                    detailsLoaded = true
                }
            }
        }
    }

    fun onEvent(event: SiteDiaryEvent) {
        val currentDetails = _uiState.value.diaryDetails ?: return
        val currentDiary = currentDetails.diary
        
        when (event) {
            is SiteDiaryEvent.UpdateWeather -> updateDiary(currentDiary.copy(weather = event.weather))
            is SiteDiaryEvent.UpdateOverallProgress -> updateDiary(currentDiary.copy(overallProgress = event.progress))
            is SiteDiaryEvent.UpdateWorkSummary -> updateDiary(currentDiary.copy(workSummary = event.summary))
            is SiteDiaryEvent.UpdateEngineerNotes -> updateDiary(currentDiary.copy(engineerNotes = event.notes))
            is SiteDiaryEvent.UpdateSafetyObservations -> updateDiary(currentDiary.copy(safetyObservations = event.observations))
            is SiteDiaryEvent.UpdateNextDayPlan -> updateDiary(currentDiary.copy(nextDayPlan = event.plan))
            
            // Note: In a real app, adding items would open a dialog, collect data, and send it here.
            is SiteDiaryEvent.AddWorkItem -> {
                val newWorkItem = WorkItem(
                    id = UUID.randomUUID().toString(),
                    siteDiaryId = currentDiary.id,
                    description = event.description,
                    percentageComplete = event.percentage,
                    remarks = event.remarks
                )
                val newItems = currentDetails.workItems + newWorkItem
                updateDetails(currentDetails.copy(workItems = newItems))
            }
            is SiteDiaryEvent.AddIssue -> {
                val newIssue = SiteIssue(
                    id = UUID.randomUUID().toString(),
                    siteDiaryId = currentDiary.id,
                    type = event.type,
                    description = event.description,
                    resolved = false
                )
                val newIssues = currentDetails.issues + newIssue
                updateDetails(currentDetails.copy(issues = newIssues))
            }
            // For now, to keep it simple, we just save immediately on any change
            is SiteDiaryEvent.Save -> saveChanges()
        }
    }

    private fun updateDiary(diary: SiteDiary) {
        val currentDetails = _uiState.value.diaryDetails ?: return
        updateDetails(currentDetails.copy(diary = diary))
    }

    private fun updateDetails(details: SiteDiaryDetails) {
        _uiState.update { it.copy(diaryDetails = details) }
    }

    private fun saveChanges() {
        val detailsToSave = _uiState.value.diaryDetails ?: return
        isSaving = true
        viewModelScope.launch {
            saveSiteDiaryDetailsUseCase(detailsToSave)
            _uiState.update { it.copy(isSaved = true) }
            isSaving = false
        }
    }
}

data class SiteDiaryDetailsUiState(
    val project: Project? = null,
    val diaryDetails: SiteDiaryDetails? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

sealed class SiteDiaryEvent {
    data class UpdateWeather(val weather: String) : SiteDiaryEvent()
    data class UpdateOverallProgress(val progress: Int) : SiteDiaryEvent()
    data class UpdateWorkSummary(val summary: String) : SiteDiaryEvent()
    data class UpdateEngineerNotes(val notes: String) : SiteDiaryEvent()
    data class UpdateSafetyObservations(val observations: String) : SiteDiaryEvent()
    data class UpdateNextDayPlan(val plan: String) : SiteDiaryEvent()
    
    data class AddWorkItem(val description: String, val percentage: Int, val remarks: String?) : SiteDiaryEvent()
    data class AddIssue(val type: IssueType, val description: String) : SiteDiaryEvent()
    
    object Save : SiteDiaryEvent()
}

class SiteDiaryDetailsViewModelFactory(
    private val diaryId: String,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getSiteDiaryDetailsUseCase: GetSiteDiaryDetailsUseCase,
    private val saveSiteDiaryDetailsUseCase: SaveSiteDiaryDetailsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SiteDiaryDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SiteDiaryDetailsViewModel(
                diaryId,
                getProjectByIdUseCase,
                getSiteDiaryDetailsUseCase,
                saveSiteDiaryDetailsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
