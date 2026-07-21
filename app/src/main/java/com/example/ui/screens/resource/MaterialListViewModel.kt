package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Material
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaterialListViewModel(
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialListUiState(isLoading = true))
    val uiState: StateFlow<MaterialListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllMaterials().collect { materials ->
                _uiState.update { 
                    it.copy(
                        materials = materials,
                        isLoading = false
                    ) 
                }
            }
        }
    }
}

data class MaterialListUiState(
    val materials: List<Material> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MaterialListViewModelFactory(
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaterialListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaterialListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
