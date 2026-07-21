package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Supplier
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupplierListViewModel(
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupplierListUiState(isLoading = true))
    val uiState: StateFlow<SupplierListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllSuppliers().collect { suppliers ->
                _uiState.update { 
                    it.copy(
                        suppliers = suppliers,
                        isLoading = false
                    ) 
                }
            }
        }
    }
}

data class SupplierListUiState(
    val suppliers: List<Supplier> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SupplierListViewModelFactory(
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupplierListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SupplierListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
