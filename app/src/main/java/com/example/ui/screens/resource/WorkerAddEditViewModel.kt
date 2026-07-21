package com.example.ui.screens.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import com.example.core.util.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class WorkerAddEditViewModel(
    private val workerId: String?,
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerAddEditUiState())
    val uiState: StateFlow<WorkerAddEditUiState> = _uiState.asStateFlow()

    init {
        if (workerId != null) {
            loadWorker(workerId)
        }
    }

    private fun loadWorker(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val worker = repository.getWorkerById(id)
            if (worker != null) {
                _uiState.update {
                    it.copy(
                        fullName = worker.fullName,
                        mobileNumber = worker.mobileNumber,
                        trade = worker.trade,
                        dailyWage = CurrencyUtils.paiseToDisplayString(worker.dailyWagePaise),
                        experience = worker.experience ?: "",
                        emergencyContact = worker.emergencyContact ?: "",
                        address = worker.address ?: "",
                        status = worker.status,
                        isLoading = false,
                        isEditing = true
                    )
                }
            } else {
                _uiState.update { it.copy(error = "Worker not found", isLoading = false) }
            }
        }
    }

    fun onEvent(event: WorkerEvent) {
        when (event) {
            is WorkerEvent.UpdateFullName -> _uiState.update { it.copy(fullName = event.name) }
            is WorkerEvent.UpdateMobileNumber -> _uiState.update { it.copy(mobileNumber = event.mobile) }
            is WorkerEvent.UpdateTrade -> _uiState.update { it.copy(trade = event.trade) }
            is WorkerEvent.UpdateDailyWage -> _uiState.update { it.copy(dailyWage = event.wage) }
            is WorkerEvent.UpdateExperience -> _uiState.update { it.copy(experience = event.exp) }
            is WorkerEvent.UpdateEmergencyContact -> _uiState.update { it.copy(emergencyContact = event.contact) }
            is WorkerEvent.UpdateAddress -> _uiState.update { it.copy(address = event.address) }
            is WorkerEvent.Save -> saveWorker()
            is WorkerEvent.Delete -> deleteWorker()
        }
    }

    private fun saveWorker() {
        val state = _uiState.value
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val worker = Worker(
                id = workerId ?: UUID.randomUUID().toString(),
                fullName = state.fullName,
                mobileNumber = state.mobileNumber,
                trade = state.trade,
                dailyWagePaise = CurrencyUtils.displayStringToPaise(state.dailyWage) ?: 0L,
                experience = state.experience.takeIf { it.isNotBlank() },
                joiningDate = System.currentTimeMillis(),
                emergencyContact = state.emergencyContact.takeIf { it.isNotBlank() },
                address = state.address.takeIf { it.isNotBlank() },
                status = state.status,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            repository.saveWorker(worker)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    private fun deleteWorker() {
        if (workerId != null) {
            viewModelScope.launch {
                repository.deleteWorker(workerId)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }
}

data class WorkerAddEditUiState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val trade: String = "Mason",
    val dailyWage: String = "",
    val experience: String = "",
    val emergencyContact: String = "",
    val address: String = "",
    val status: String = "ACTIVE",
    
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null
)

sealed class WorkerEvent {
    data class UpdateFullName(val name: String) : WorkerEvent()
    data class UpdateMobileNumber(val mobile: String) : WorkerEvent()
    data class UpdateTrade(val trade: String) : WorkerEvent()
    data class UpdateDailyWage(val wage: String) : WorkerEvent()
    data class UpdateExperience(val exp: String) : WorkerEvent()
    data class UpdateEmergencyContact(val contact: String) : WorkerEvent()
    data class UpdateAddress(val address: String) : WorkerEvent()
    object Save : WorkerEvent()
    object Delete : WorkerEvent()
}

class WorkerAddEditViewModelFactory(
    private val workerId: String?,
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkerAddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkerAddEditViewModel(workerId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
