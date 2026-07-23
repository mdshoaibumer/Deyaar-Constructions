package com.example.ui.screens.resource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Transaction
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import com.example.domain.repository.TransactionRepository
import com.example.ui.theme.Dimens
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WorkerPaymentHistoryUiState(
    val worker: Worker? = null,
    val payments: List<Transaction> = emptyList(),
    val totalPaidPaise: Long = 0L,
    val isLoading: Boolean = true
)

class WorkerPaymentHistoryViewModel(
    private val workerId: String,
    private val resourceRepository: ResourceRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerPaymentHistoryUiState())
    val uiState: StateFlow<WorkerPaymentHistoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val worker = resourceRepository.getWorkerById(workerId)
            _uiState.update { it.copy(worker = worker) }

            // Get all labour payment transactions that reference this worker
            transactionRepository.getAllTransactions().collect { allTransactions ->
                val workerPayments = allTransactions.filter { t ->
                    t.category.name == "LABOUR_PAYMENT" && !t.isDeleted &&
                    (t.description?.contains(worker?.fullName ?: "", ignoreCase = true) == true ||
                     t.description?.contains(workerId, ignoreCase = true) == true)
                }
                val totalPaid = workerPayments.sumOf { it.amountPaise }
                _uiState.update {
                    it.copy(
                        payments = workerPayments,
                        totalPaidPaise = totalPaid,
                        isLoading = false
                    )
                }
            }
        }
    }
}

class WorkerPaymentHistoryViewModelFactory(
    private val workerId: String,
    private val resourceRepository: ResourceRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WorkerPaymentHistoryViewModel(workerId, resourceRepository, transactionRepository) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPaymentHistoryScreen(
    viewModel: WorkerPaymentHistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.worker?.fullName ?: "Payment History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.ShimmerCardList(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(Dimens.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                // Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                            Text("Payment Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Total Paid", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        CurrencyUtils.formatPaise(uiState.totalPaidPaise),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Daily Wage", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        CurrencyUtils.formatPaise(uiState.worker?.dailyWagePaise ?: 0L),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                            Text(
                                "Payments: ${uiState.payments.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Text("Payment Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                if (uiState.payments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No payment records found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(uiState.payments, key = { it.id }) { payment ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(Dimens.spaceMedium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dateFormatter.format(Date(payment.date)),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (!payment.description.isNullOrBlank()) {
                                        Text(payment.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = payment.paymentMethod.name.replace("_", " "),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (!payment.referenceNumber.isNullOrBlank()) {
                                        Text("Ref: ${payment.referenceNumber}", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Text(
                                    CurrencyUtils.formatPaise(payment.amountPaise),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.DeyaarTheme.colors.success
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
