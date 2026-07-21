package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkerListViewModel(
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerListUiState(isLoading = true))
    val uiState: StateFlow<WorkerListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllWorkers().collect { workers ->
                _uiState.update { 
                    it.copy(
                        workers = workers,
                        isLoading = false
                    ) 
                }
            }
        }
    }
}

data class WorkerListUiState(
    val workers: List<Worker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class WorkerListViewModelFactory(
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkerListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkerListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
