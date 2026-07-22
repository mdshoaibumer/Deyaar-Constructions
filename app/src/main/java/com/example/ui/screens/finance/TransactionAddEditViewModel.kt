package com.example.ui.screens.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.domain.model.*
import com.example.domain.usecase.finance.GetTransactionByIdUseCase
import com.example.domain.usecase.finance.SaveTransactionUseCase
import com.example.domain.usecase.finance.DeleteTransactionUseCase
import com.example.core.util.CurrencyUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionAddEditViewModel(
    private val transactionId: String?,
    private val projectId: String,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreState() ?: TransactionAddEditUiState())
    val uiState: StateFlow<TransactionAddEditUiState> = _uiState.asStateFlow()

    init {
        if (transactionId != null && !savedStateHandle.contains(KEY_TYPE)) {
            loadTransaction(transactionId)
        } else if (transactionId == null && !savedStateHandle.contains(KEY_TYPE)) {
            _uiState.update { it.copy(date = System.currentTimeMillis(), time = System.currentTimeMillis()) }
            saveState()
        }
    }

    private fun loadTransaction(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val transaction = getTransactionByIdUseCase(id)
            if (transaction != null) {
                _uiState.update {
                    it.copy(
                        type = transaction.type,
                        category = transaction.category,
                        amount = CurrencyUtils.paiseToDisplayString(transaction.amountPaise),
                        paymentMethod = transaction.paymentMethod,
                        referenceNumber = transaction.referenceNumber ?: "",
                        description = transaction.description ?: "",
                        date = transaction.date,
                        time = transaction.time,
                        isLoading = false,
                        isEditing = true
                    )
                }
                saveState()
            } else {
                _uiState.update { it.copy(error = "Transaction not found", isLoading = false) }
            }
        }
    }

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.UpdateType -> _uiState.update { it.copy(type = event.type) }
            is TransactionEvent.UpdateCategory -> _uiState.update { it.copy(category = event.category) }
            is TransactionEvent.UpdateAmount -> _uiState.update { it.copy(amount = event.amount) }
            is TransactionEvent.UpdatePaymentMethod -> _uiState.update { it.copy(paymentMethod = event.method) }
            is TransactionEvent.UpdateReferenceNumber -> _uiState.update { it.copy(referenceNumber = event.ref) }
            is TransactionEvent.UpdateDescription -> _uiState.update { it.copy(description = event.desc) }
            is TransactionEvent.UpdateDate -> _uiState.update { it.copy(date = event.date) }
            is TransactionEvent.UpdateTime -> _uiState.update { it.copy(time = event.time) }
            is TransactionEvent.Save -> saveTransaction()
            is TransactionEvent.Delete -> deleteTransaction()
        }
        saveState()
    }

    private fun saveState() {
        val state = _uiState.value
        savedStateHandle[KEY_TYPE] = state.type.name
        savedStateHandle[KEY_CATEGORY] = state.category.name
        savedStateHandle[KEY_AMOUNT] = state.amount
        savedStateHandle[KEY_PAYMENT_METHOD] = state.paymentMethod.name
        savedStateHandle[KEY_REFERENCE_NUMBER] = state.referenceNumber
        savedStateHandle[KEY_DESCRIPTION] = state.description
        savedStateHandle[KEY_DATE] = state.date
        savedStateHandle[KEY_TIME] = state.time
    }

    private fun restoreState(): TransactionAddEditUiState? {
        val type = savedStateHandle.get<String>(KEY_TYPE) ?: return null
        return TransactionAddEditUiState(
            type = try { TransactionType.valueOf(type) } catch (_: Exception) { TransactionType.EXPENSE },
            category = savedStateHandle.get<String>(KEY_CATEGORY)?.let {
                try { TransactionCategory.valueOf(it) } catch (_: Exception) { TransactionCategory.MATERIAL_PURCHASE }
            } ?: TransactionCategory.MATERIAL_PURCHASE,
            amount = savedStateHandle[KEY_AMOUNT] ?: "",
            paymentMethod = savedStateHandle.get<String>(KEY_PAYMENT_METHOD)?.let {
                try { PaymentMethod.valueOf(it) } catch (_: Exception) { PaymentMethod.CASH }
            } ?: PaymentMethod.CASH,
            referenceNumber = savedStateHandle[KEY_REFERENCE_NUMBER] ?: "",
            description = savedStateHandle[KEY_DESCRIPTION] ?: "",
            date = savedStateHandle[KEY_DATE] ?: 0L,
            time = savedStateHandle[KEY_TIME] ?: 0L
        )
    }

    private fun saveTransaction() {
        val state = _uiState.value
        val amountPaise = CurrencyUtils.displayStringToPaise(state.amount)
        
        if (amountPaise == null || amountPaise <= 0L) {
            _uiState.update { it.copy(error = "Invalid amount") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }
            
                val transaction = Transaction(
                    id = transactionId ?: UUID.randomUUID().toString(),
                    projectId = projectId,
                    date = state.date,
                    time = state.time,
                    type = state.type,
                    category = state.category,
                    amountPaise = amountPaise,
                    paymentMethod = state.paymentMethod,
                    referenceNumber = state.referenceNumber.takeIf { it.isNotBlank() },
                    description = state.description.takeIf { it.isNotBlank() },
                    createdBy = "Owner",
                    isDeleted = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    attachmentPath = null
                )
            
                saveTransactionUseCase(transaction)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "An error occurred") }
            }
        }
    }

    private fun deleteTransaction() {
        if (transactionId != null) {
            viewModelScope.launch {
                try {
                    deleteTransactionUseCase(transactionId)
                    _uiState.update { it.copy(isSaved = true) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "An error occurred") }
                }
            }
        }
    }

    companion object {
        private const val KEY_TYPE = "txn_type"
        private const val KEY_CATEGORY = "txn_category"
        private const val KEY_AMOUNT = "txn_amount"
        private const val KEY_PAYMENT_METHOD = "txn_paymentMethod"
        private const val KEY_REFERENCE_NUMBER = "txn_referenceNumber"
        private const val KEY_DESCRIPTION = "txn_description"
        private const val KEY_DATE = "txn_date"
        private const val KEY_TIME = "txn_time"
    }
}

data class TransactionAddEditUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.MATERIAL_PURCHASE,
    val amount: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val referenceNumber: String = "",
    val description: String = "",
    val date: Long = 0,
    val time: Long = 0,
    
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null
)

sealed class TransactionEvent {
    data class UpdateType(val type: TransactionType) : TransactionEvent()
    data class UpdateCategory(val category: TransactionCategory) : TransactionEvent()
    data class UpdateAmount(val amount: String) : TransactionEvent()
    data class UpdatePaymentMethod(val method: PaymentMethod) : TransactionEvent()
    data class UpdateReferenceNumber(val ref: String) : TransactionEvent()
    data class UpdateDescription(val desc: String) : TransactionEvent()
    data class UpdateDate(val date: Long) : TransactionEvent()
    data class UpdateTime(val time: Long) : TransactionEvent()
    object Save : TransactionEvent()
    object Delete : TransactionEvent()
}

class TransactionAddEditViewModelFactory(
    private val transactionId: String?,
    private val projectId: String,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(TransactionAddEditViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return TransactionAddEditViewModel(
                transactionId, projectId, getTransactionByIdUseCase, saveTransactionUseCase, deleteTransactionUseCase, savedStateHandle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
