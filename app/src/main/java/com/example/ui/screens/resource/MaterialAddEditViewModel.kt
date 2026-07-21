package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Material
import com.example.domain.repository.ResourceRepository
import com.example.core.util.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MaterialAddEditViewModel(
    private val materialId: String?,
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialAddEditUiState())
    val uiState: StateFlow<MaterialAddEditUiState> = _uiState.asStateFlow()

    init {
        if (materialId != null) {
            loadMaterial(materialId)
        }
    }

    private fun loadMaterial(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val material = repository.getMaterialById(id)
            if (material != null) {
                _uiState.update {
                    it.copy(
                        name = material.name,
                        category = material.category,
                        unit = material.unit,
                        currentStock = material.currentStock.toString(),
                        minimumStock = material.minimumStock.toString(),
                        openingStock = material.openingStock.toString(),
                        purchasePrice = CurrencyUtils.paiseToDisplayString(material.purchasePricePaise),
                        averageCost = CurrencyUtils.paiseToDisplayString(material.averageCostPaise),
                        remarks = material.remarks ?: "",
                        status = material.status,
                        isLoading = false,
                        isEditing = true
                    )
                }
            } else {
                _uiState.update { it.copy(error = "Material not found", isLoading = false) }
            }
        }
    }

    fun onEvent(event: MaterialEvent) {
        when (event) {
            is MaterialEvent.UpdateName -> _uiState.update { it.copy(name = event.name) }
            is MaterialEvent.UpdateCategory -> _uiState.update { it.copy(category = event.category) }
            is MaterialEvent.UpdateUnit -> _uiState.update { it.copy(unit = event.unit) }
            is MaterialEvent.UpdateCurrentStock -> _uiState.update { it.copy(currentStock = event.stock) }
            is MaterialEvent.UpdateMinimumStock -> _uiState.update { it.copy(minimumStock = event.stock) }
            is MaterialEvent.UpdatePurchasePrice -> _uiState.update { it.copy(purchasePrice = event.price) }
            is MaterialEvent.UpdateRemarks -> _uiState.update { it.copy(remarks = event.remarks) }
            is MaterialEvent.Save -> saveMaterial()
            is MaterialEvent.Delete -> deleteMaterial()
        }
    }

    private fun saveMaterial() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val material = Material(
                id = materialId ?: UUID.randomUUID().toString(),
                name = state.name,
                category = state.category,
                unit = state.unit,
                currentStock = state.currentStock.toDoubleOrNull() ?: 0.0,
                minimumStock = state.minimumStock.toDoubleOrNull() ?: 0.0,
                openingStock = state.openingStock.toDoubleOrNull() ?: 0.0,
                purchasePricePaise = CurrencyUtils.displayStringToPaise(state.purchasePrice) ?: 0L,
                averageCostPaise = CurrencyUtils.displayStringToPaise(state.averageCost) ?: 0L,
                remarks = state.remarks.takeIf { it.isNotBlank() },
                status = state.status,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            repository.saveMaterial(material)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    private fun deleteMaterial() {
        if (materialId != null) {
            viewModelScope.launch {
                repository.deleteMaterial(materialId)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }
}

data class MaterialAddEditUiState(
    val name: String = "",
    val category: String = "Cement",
    val unit: String = "kg",
    val currentStock: String = "",
    val minimumStock: String = "",
    val openingStock: String = "0",
    val purchasePrice: String = "",
    val averageCost: String = "0",
    val remarks: String = "",
    val status: String = "ACTIVE",
    
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null
)

sealed class MaterialEvent {
    data class UpdateName(val name: String) : MaterialEvent()
    data class UpdateCategory(val category: String) : MaterialEvent()
    data class UpdateUnit(val unit: String) : MaterialEvent()
    data class UpdateCurrentStock(val stock: String) : MaterialEvent()
    data class UpdateMinimumStock(val stock: String) : MaterialEvent()
    data class UpdatePurchasePrice(val price: String) : MaterialEvent()
    data class UpdateRemarks(val remarks: String) : MaterialEvent()
    object Save : MaterialEvent()
    object Delete : MaterialEvent()
}

class MaterialAddEditViewModelFactory(
    private val materialId: String?,
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaterialAddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaterialAddEditViewModel(materialId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
