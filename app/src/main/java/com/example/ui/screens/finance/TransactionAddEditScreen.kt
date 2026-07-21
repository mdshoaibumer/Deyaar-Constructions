package com.example.ui.screens.finance

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.PaymentMethod
import com.example.domain.model.TransactionCategory
import com.example.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAddEditScreen(
    viewModel: TransactionAddEditViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Transaction" else "Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.spaceMedium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            // Transaction Type (Income / Expense / Adjustment)
            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }
            ) {
                OutlinedTextField(
                    value = uiState.type.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    TransactionType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                viewModel.onEvent(TransactionEvent.UpdateType(type))
                                expandedType = false
                            }
                        )
                    }
                }
            }
            
            // Category
            var expandedCategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = uiState.category.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    TransactionCategory.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name.replace("_", " ")) },
                            onClick = {
                                viewModel.onEvent(TransactionEvent.UpdateCategory(cat))
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onEvent(TransactionEvent.UpdateAmount(it)) },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error == "Invalid amount"
            )
            
            // Payment Method
            var expandedMethod by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedMethod,
                onExpandedChange = { expandedMethod = !expandedMethod }
            ) {
                OutlinedTextField(
                    value = uiState.paymentMethod.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMethod) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedMethod,
                    onDismissRequest = { expandedMethod = false }
                ) {
                    PaymentMethod.values().forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method.name.replace("_", " ")) },
                            onClick = {
                                viewModel.onEvent(TransactionEvent.UpdatePaymentMethod(method))
                                expandedMethod = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.referenceNumber,
                onValueChange = { viewModel.onEvent(TransactionEvent.UpdateReferenceNumber(it)) },
                label = { Text("Reference Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onEvent(TransactionEvent.UpdateDescription(it)) },
                label = { Text("Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (uiState.error != null && uiState.error != "Invalid amount") {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onEvent(TransactionEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Transaction")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.onEvent(TransactionEvent.Delete)
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
