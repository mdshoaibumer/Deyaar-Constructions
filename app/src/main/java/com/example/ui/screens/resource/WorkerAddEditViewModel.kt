package com.example.ui.screens.resource

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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
    private val repository: ResourceRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreState() ?: WorkerAddEditUiState())
    val uiState: StateFlow<WorkerAddEditUiState> = _uiState.asStateFlow()

    init {
        if (workerId != null && !savedStateHandle.contains(KEY_FULL_NAME)) {
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
                saveState()
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
        saveState()
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_FULL_NAME] = state.fullName
        savedStateHandle[KEY_MOBILE_NUMBER] = state.mobileNumber
        savedStateHandle[KEY_TRADE] = state.trade
        savedStateHandle[KEY_DAILY_WAGE] = state.dailyWage
        savedStateHandle[KEY_EXPERIENCE] = state.experience
        savedStateHandle[KEY_EMERGENCY_CONTACT] = state.emergencyContact
        savedStateHandle[KEY_ADDRESS] = state.address
        savedStateHandle[KEY_STATUS] = state.status
    }

    private fun restoreState(): WorkerAddEditUiState? {
        val fullName = savedStateHandle.get<String>(KEY_FULL_NAME) ?: return null
        return WorkerAddEditUiState(
            fullName = fullName,
            mobileNumber = savedStateHandle[KEY_MOBILE_NUMBER] ?: "",
            trade = savedStateHandle[KEY_TRADE] ?: "Mason",
            dailyWage = savedStateHandle[KEY_DAILY_WAGE] ?: "",
            experience = savedStateHandle[KEY_EXPERIENCE] ?: "",
            emergencyContact = savedStateHandle[KEY_EMERGENCY_CONTACT] ?: "",
            address = savedStateHandle[KEY_ADDRESS] ?: "",
            status = savedStateHandle[KEY_STATUS] ?: "ACTIVE"
        )
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

    companion object {
        private const val KEY_FULL_NAME = "worker_fullName"
        private const val KEY_MOBILE_NUMBER = "worker_mobileNumber"
        private const val KEY_TRADE = "worker_trade"
        private const val KEY_DAILY_WAGE = "worker_dailyWage"
        private const val KEY_EXPERIENCE = "worker_experience"
        private const val KEY_EMERGENCY_CONTACT = "worker_emergencyContact"
        private const val KEY_ADDRESS = "worker_address"
        private const val KEY_STATUS = "worker_status"
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
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(WorkerAddEditViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return WorkerAddEditViewModel(workerId, repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
