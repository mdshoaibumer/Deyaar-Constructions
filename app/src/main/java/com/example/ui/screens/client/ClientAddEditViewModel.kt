package com.example.ui.screens.client

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.domain.model.Client
import com.example.domain.model.ClientCategory
import com.example.domain.usecase.client.GetClientByIdUseCase
import com.example.domain.usecase.client.SaveClientUseCase
import com.example.domain.usecase.client.ValidateClientPhoneUseCase
import com.example.domain.usecase.client.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ClientAddEditViewModel(
    private val clientId: String?,
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val saveClientUseCase: SaveClientUseCase,
    private val validateClientPhoneUseCase: ValidateClientPhoneUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreState() ?: ClientAddEditUiState())
    val uiState: StateFlow<ClientAddEditUiState> = _uiState.asStateFlow()

    init {
        if (clientId != null && !savedStateHandle.contains(KEY_NAME)) {
            loadClient(clientId)
        }
    }

    private fun loadClient(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val client = getClientByIdUseCase(id)
            if (client != null) {
                _uiState.update {
                    it.copy(
                        name = client.name,
                        companyName = client.companyName ?: "",
                        phone = client.phone,
                        altPhone = client.altPhone ?: "",
                        whatsapp = client.whatsapp ?: "",
                        email = client.email ?: "",
                        gstNumber = client.gstNumber ?: "",
                        panNumber = client.panNumber ?: "",
                        address = client.address ?: "",
                        city = client.city ?: "",
                        state = client.state ?: "",
                        pincode = client.pincode ?: "",
                        category = client.category,
                        notes = client.notes ?: "",
                        isActive = client.isActive,
                        isLoading = false
                    )
                }
                saveState()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Client not found") }
            }
        }
    }

    fun onEvent(event: ClientAddEditEvent) {
        when (event) {
            is ClientAddEditEvent.NameChanged -> _uiState.update { it.copy(name = event.name, error = null) }
            is ClientAddEditEvent.CompanyNameChanged -> _uiState.update { it.copy(companyName = event.name) }
            is ClientAddEditEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.phone, error = null) }
            is ClientAddEditEvent.AltPhoneChanged -> _uiState.update { it.copy(altPhone = event.phone) }
            is ClientAddEditEvent.WhatsappChanged -> _uiState.update { it.copy(whatsapp = event.whatsapp) }
            is ClientAddEditEvent.EmailChanged -> _uiState.update { it.copy(email = event.email, error = null) }
            is ClientAddEditEvent.GstChanged -> _uiState.update { it.copy(gstNumber = event.gst, error = null) }
            is ClientAddEditEvent.PanChanged -> _uiState.update { it.copy(panNumber = event.pan, error = null) }
            is ClientAddEditEvent.AddressChanged -> _uiState.update { it.copy(address = event.address) }
            is ClientAddEditEvent.CityChanged -> _uiState.update { it.copy(city = event.city) }
            is ClientAddEditEvent.StateChanged -> _uiState.update { it.copy(state = event.state) }
            is ClientAddEditEvent.PincodeChanged -> _uiState.update { it.copy(pincode = event.pincode) }
            is ClientAddEditEvent.CategoryChanged -> _uiState.update { it.copy(category = event.category) }
            is ClientAddEditEvent.NotesChanged -> _uiState.update { it.copy(notes = event.notes) }
            is ClientAddEditEvent.IsActiveChanged -> _uiState.update { it.copy(isActive = event.isActive) }
            is ClientAddEditEvent.Save -> saveClient()
        }
        saveState()
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_NAME] = state.name
        savedStateHandle[KEY_COMPANY_NAME] = state.companyName
        savedStateHandle[KEY_PHONE] = state.phone
        savedStateHandle[KEY_ALT_PHONE] = state.altPhone
        savedStateHandle[KEY_WHATSAPP] = state.whatsapp
        savedStateHandle[KEY_EMAIL] = state.email
        savedStateHandle[KEY_GST] = state.gstNumber
        savedStateHandle[KEY_PAN] = state.panNumber
        savedStateHandle[KEY_ADDRESS] = state.address
        savedStateHandle[KEY_CITY] = state.city
        savedStateHandle[KEY_STATE] = state.state
        savedStateHandle[KEY_PINCODE] = state.pincode
        savedStateHandle[KEY_CATEGORY] = state.category.name
        savedStateHandle[KEY_NOTES] = state.notes
        savedStateHandle[KEY_IS_ACTIVE] = state.isActive
    }

    private fun restoreState(): ClientAddEditUiState? {
        val name = savedStateHandle.get<String>(KEY_NAME) ?: return null
        return ClientAddEditUiState(
            name = name,
            companyName = savedStateHandle[KEY_COMPANY_NAME] ?: "",
            phone = savedStateHandle[KEY_PHONE] ?: "",
            altPhone = savedStateHandle[KEY_ALT_PHONE] ?: "",
            whatsapp = savedStateHandle[KEY_WHATSAPP] ?: "",
            email = savedStateHandle[KEY_EMAIL] ?: "",
            gstNumber = savedStateHandle[KEY_GST] ?: "",
            panNumber = savedStateHandle[KEY_PAN] ?: "",
            address = savedStateHandle[KEY_ADDRESS] ?: "",
            city = savedStateHandle[KEY_CITY] ?: "",
            state = savedStateHandle[KEY_STATE] ?: "",
            pincode = savedStateHandle[KEY_PINCODE] ?: "",
            category = savedStateHandle.get<String>(KEY_CATEGORY)?.let {
                try { ClientCategory.valueOf(it) } catch (_: Exception) { ClientCategory.RESIDENTIAL }
            } ?: ClientCategory.RESIDENTIAL,
            notes = savedStateHandle[KEY_NOTES] ?: "",
            isActive = savedStateHandle[KEY_IS_ACTIVE] ?: true
        )
    }

    private fun saveClient() {
        val state = _uiState.value
        
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Name is required") }
            return
        }

        if (!ValidationUtils.isValidPhone(state.phone.trim())) {
            _uiState.update { it.copy(error = "Invalid phone number (must be 10 digits)") }
            return
        }

        if (state.email.isNotBlank() && !ValidationUtils.isValidEmail(state.email.trim())) {
            _uiState.update { it.copy(error = "Invalid email format") }
            return
        }
        
        if (state.gstNumber.isNotBlank() && !ValidationUtils.isValidGst(state.gstNumber.trim())) {
            _uiState.update { it.copy(error = "Invalid GST format") }
            return
        }

        if (state.panNumber.isNotBlank() && !ValidationUtils.isValidPan(state.panNumber.trim())) {
            _uiState.update { it.copy(error = "Invalid PAN format") }
            return
        }

        viewModelScope.launch {
            try {
                val isDuplicate = validateClientPhoneUseCase(state.phone.trim(), clientId)
                if (isDuplicate) {
                    _uiState.update { it.copy(error = "A client with this phone number already exists") }
                    return@launch
                }

                val client = Client(
                    id = clientId ?: UUID.randomUUID().toString(),
                    name = state.name.trim(),
                    companyName = state.companyName.trim().takeIf { it.isNotBlank() },
                    phone = state.phone.trim(),
                    altPhone = state.altPhone.trim().takeIf { it.isNotBlank() },
                    whatsapp = state.whatsapp.trim().takeIf { it.isNotBlank() },
                    email = state.email.trim().takeIf { it.isNotBlank() },
                    gstNumber = state.gstNumber.trim().takeIf { it.isNotBlank() },
                    panNumber = state.panNumber.trim().takeIf { it.isNotBlank() },
                    address = state.address.trim().takeIf { it.isNotBlank() },
                    city = state.city.trim().takeIf { it.isNotBlank() },
                    state = state.state.trim().takeIf { it.isNotBlank() },
                    pincode = state.pincode.trim().takeIf { it.isNotBlank() },
                    category = state.category,
                    notes = state.notes.trim().takeIf { it.isNotBlank() },
                    isActive = state.isActive,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                saveClientUseCase(client)
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred") }
            }
        }
    }

    companion object {
        private const val KEY_NAME = "client_name"
        private const val KEY_COMPANY_NAME = "client_company_name"
        private const val KEY_PHONE = "client_phone"
        private const val KEY_ALT_PHONE = "client_alt_phone"
        private const val KEY_WHATSAPP = "client_whatsapp"
        private const val KEY_EMAIL = "client_email"
        private const val KEY_GST = "client_gst"
        private const val KEY_PAN = "client_pan"
        private const val KEY_ADDRESS = "client_address"
        private const val KEY_CITY = "client_city"
        private const val KEY_STATE = "client_state"
        private const val KEY_PINCODE = "client_pincode"
        private const val KEY_CATEGORY = "client_category"
        private const val KEY_NOTES = "client_notes"
        private const val KEY_IS_ACTIVE = "client_is_active"
    }
}

class ClientAddEditViewModelFactory(
    private val clientId: String?,
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val saveClientUseCase: SaveClientUseCase,
    private val validateClientPhoneUseCase: ValidateClientPhoneUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ClientAddEditViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return ClientAddEditViewModel(clientId, getClientByIdUseCase, saveClientUseCase, validateClientPhoneUseCase, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class ClientAddEditUiState(
    val name: String = "",
    val companyName: String = "",
    val phone: String = "",
    val altPhone: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val gstNumber: String = "",
    val panNumber: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val category: ClientCategory = ClientCategory.RESIDENTIAL,
    val notes: String = "",
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

sealed class ClientAddEditEvent {
    data class NameChanged(val name: String) : ClientAddEditEvent()
    data class CompanyNameChanged(val name: String) : ClientAddEditEvent()
    data class PhoneChanged(val phone: String) : ClientAddEditEvent()
    data class AltPhoneChanged(val phone: String) : ClientAddEditEvent()
    data class WhatsappChanged(val whatsapp: String) : ClientAddEditEvent()
    data class EmailChanged(val email: String) : ClientAddEditEvent()
    data class GstChanged(val gst: String) : ClientAddEditEvent()
    data class PanChanged(val pan: String) : ClientAddEditEvent()
    data class AddressChanged(val address: String) : ClientAddEditEvent()
    data class CityChanged(val city: String) : ClientAddEditEvent()
    data class StateChanged(val state: String) : ClientAddEditEvent()
    data class PincodeChanged(val pincode: String) : ClientAddEditEvent()
    data class CategoryChanged(val category: ClientCategory) : ClientAddEditEvent()
    data class NotesChanged(val notes: String) : ClientAddEditEvent()
    data class IsActiveChanged(val isActive: Boolean) : ClientAddEditEvent()
    object Save : ClientAddEditEvent()
}
