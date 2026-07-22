package com.example.ui.screens.resource

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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
    private val repository: ResourceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreState() ?: MaterialAddEditUiState())
    val uiState: StateFlow<MaterialAddEditUiState> = _uiState.asStateFlow()

    init {
        if (materialId != null && !savedStateHandle.contains(KEY_NAME)) {
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
                saveState()
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
        saveState()
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_NAME] = state.name
        savedStateHandle[KEY_CATEGORY] = state.category
        savedStateHandle[KEY_UNIT] = state.unit
        savedStateHandle[KEY_CURRENT_STOCK] = state.currentStock
        savedStateHandle[KEY_MINIMUM_STOCK] = state.minimumStock
        savedStateHandle[KEY_OPENING_STOCK] = state.openingStock
        savedStateHandle[KEY_PURCHASE_PRICE] = state.purchasePrice
        savedStateHandle[KEY_AVERAGE_COST] = state.averageCost
        savedStateHandle[KEY_REMARKS] = state.remarks
        savedStateHandle[KEY_STATUS] = state.status
    }

    private fun restoreState(): MaterialAddEditUiState? {
        val name = savedStateHandle.get<String>(KEY_NAME) ?: return null
        return MaterialAddEditUiState(
            name = name,
            category = savedStateHandle[KEY_CATEGORY] ?: "Cement",
            unit = savedStateHandle[KEY_UNIT] ?: "kg",
            currentStock = savedStateHandle[KEY_CURRENT_STOCK] ?: "",
            minimumStock = savedStateHandle[KEY_MINIMUM_STOCK] ?: "",
            openingStock = savedStateHandle[KEY_OPENING_STOCK] ?: "0",
            purchasePrice = savedStateHandle[KEY_PURCHASE_PRICE] ?: "",
            averageCost = savedStateHandle[KEY_AVERAGE_COST] ?: "0",
            remarks = savedStateHandle[KEY_REMARKS] ?: "",
            status = savedStateHandle[KEY_STATUS] ?: "ACTIVE"
        )
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

    companion object {
        private const val KEY_NAME = "material_name"
        private const val KEY_CATEGORY = "material_category"
        private const val KEY_UNIT = "material_unit"
        private const val KEY_CURRENT_STOCK = "material_currentStock"
        private const val KEY_MINIMUM_STOCK = "material_minimumStock"
        private const val KEY_OPENING_STOCK = "material_openingStock"
        private const val KEY_PURCHASE_PRICE = "material_purchasePrice"
        private const val KEY_AVERAGE_COST = "material_averageCost"
        private const val KEY_REMARKS = "material_remarks"
        private const val KEY_STATUS = "material_status"
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
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(MaterialAddEditViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return MaterialAddEditViewModel(materialId, repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
