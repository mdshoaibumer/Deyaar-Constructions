package com.example.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionAddEditUiState())
    val uiState: StateFlow<TransactionAddEditUiState> = _uiState.asStateFlow()

    init {
        if (transactionId != null) {
            loadTransaction(transactionId)
        } else {
            // New transaction defaults
            _uiState.update { it.copy(date = System.currentTimeMillis(), time = System.currentTimeMillis()) }
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
                    createdBy = "Owner", // Fixed for single user
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
                    _uiState.update { it.copy(isSaved = true) } // Navigate back
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "An error occurred") }
                }
            }
        }
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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionAddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionAddEditViewModel(
                transactionId, projectId, getTransactionByIdUseCase, saveTransactionUseCase, deleteTransactionUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
