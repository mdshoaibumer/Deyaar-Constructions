package com.example.ui.screens.resource

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.domain.model.Supplier
import com.example.domain.repository.ResourceRepository
import com.example.core.util.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SupplierAddEditViewModel(
    private val supplierId: String?,
    private val repository: ResourceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreState() ?: SupplierAddEditUiState())
    val uiState: StateFlow<SupplierAddEditUiState> = _uiState.asStateFlow()

    init {
        if (supplierId != null && !savedStateHandle.contains(KEY_NAME)) {
            loadSupplier(supplierId)
        }
    }

    private fun loadSupplier(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val supplier = repository.getSupplierById(id)
            if (supplier != null) {
                _uiState.update {
                    it.copy(
                        name = supplier.name,
                        phone = supplier.phone,
                        gst = supplier.gst ?: "",
                        address = supplier.address ?: "",
                        materialCategories = supplier.materialCategories.joinToString(", "),
                        outstandingBalance = CurrencyUtils.paiseToDisplayString(supplier.outstandingBalancePaise),
                        notes = supplier.notes ?: "",
                        isLoading = false,
                        isEditing = true
                    )
                }
                saveState()
            } else {
                _uiState.update { it.copy(error = "Supplier not found", isLoading = false) }
            }
        }
    }

    fun onEvent(event: SupplierEvent) {
        when (event) {
            is SupplierEvent.UpdateName -> _uiState.update { it.copy(name = event.name) }
            is SupplierEvent.UpdatePhone -> _uiState.update { it.copy(phone = event.phone) }
            is SupplierEvent.UpdateGst -> _uiState.update { it.copy(gst = event.gst) }
            is SupplierEvent.UpdateAddress -> _uiState.update { it.copy(address = event.address) }
            is SupplierEvent.UpdateMaterialCategories -> _uiState.update { it.copy(materialCategories = event.categories) }
            is SupplierEvent.UpdateOutstandingBalance -> _uiState.update { it.copy(outstandingBalance = event.balance) }
            is SupplierEvent.UpdateNotes -> _uiState.update { it.copy(notes = event.notes) }
            is SupplierEvent.Save -> saveSupplier()
            is SupplierEvent.Delete -> deleteSupplier()
        }
        saveState()
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_NAME] = state.name
        savedStateHandle[KEY_PHONE] = state.phone
        savedStateHandle[KEY_GST] = state.gst
        savedStateHandle[KEY_ADDRESS] = state.address
        savedStateHandle[KEY_MATERIAL_CATEGORIES] = state.materialCategories
        savedStateHandle[KEY_OUTSTANDING_BALANCE] = state.outstandingBalance
        savedStateHandle[KEY_NOTES] = state.notes
    }

    private fun restoreState(): SupplierAddEditUiState? {
        val name = savedStateHandle.get<String>(KEY_NAME) ?: return null
        return SupplierAddEditUiState(
            name = name,
            phone = savedStateHandle[KEY_PHONE] ?: "",
            gst = savedStateHandle[KEY_GST] ?: "",
            address = savedStateHandle[KEY_ADDRESS] ?: "",
            materialCategories = savedStateHandle[KEY_MATERIAL_CATEGORIES] ?: "",
            outstandingBalance = savedStateHandle[KEY_OUTSTANDING_BALANCE] ?: "0",
            notes = savedStateHandle[KEY_NOTES] ?: ""
        )
    }

    private fun saveSupplier() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val supplier = Supplier(
                id = supplierId ?: UUID.randomUUID().toString(),
                name = state.name,
                phone = state.phone,
                gst = state.gst.takeIf { it.isNotBlank() },
                address = state.address.takeIf { it.isNotBlank() },
                materialCategories = state.materialCategories.split(",").map { it.trim() }.filter { it.isNotBlank() },
                outstandingBalancePaise = CurrencyUtils.displayStringToPaise(state.outstandingBalance) ?: 0L,
                notes = state.notes.takeIf { it.isNotBlank() },
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            repository.saveSupplier(supplier)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    private fun deleteSupplier() {
        if (supplierId != null) {
            viewModelScope.launch {
                repository.deleteSupplier(supplierId)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    companion object {
        private const val KEY_NAME = "supplier_name"
        private const val KEY_PHONE = "supplier_phone"
        private const val KEY_GST = "supplier_gst"
        private const val KEY_ADDRESS = "supplier_address"
        private const val KEY_MATERIAL_CATEGORIES = "supplier_materialCategories"
        private const val KEY_OUTSTANDING_BALANCE = "supplier_outstandingBalance"
        private const val KEY_NOTES = "supplier_notes"
    }
}

data class SupplierAddEditUiState(
    val name: String = "",
    val phone: String = "",
    val gst: String = "",
    val address: String = "",
    val materialCategories: String = "",
    val outstandingBalance: String = "0",
    val notes: String = "",
    
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null
)

sealed class SupplierEvent {
    data class UpdateName(val name: String) : SupplierEvent()
    data class UpdatePhone(val phone: String) : SupplierEvent()
    data class UpdateGst(val gst: String) : SupplierEvent()
    data class UpdateAddress(val address: String) : SupplierEvent()
    data class UpdateMaterialCategories(val categories: String) : SupplierEvent()
    data class UpdateOutstandingBalance(val balance: String) : SupplierEvent()
    data class UpdateNotes(val notes: String) : SupplierEvent()
    object Save : SupplierEvent()
    object Delete : SupplierEvent()
}

class SupplierAddEditViewModelFactory(
    private val supplierId: String?,
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(SupplierAddEditViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return SupplierAddEditViewModel(supplierId, repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
