package com.example.ui.screens.resource

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierAddEditScreen(
    viewModel: SupplierAddEditViewModel,
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
                title = { Text(if (uiState.isEditing) "Edit Supplier" else "Add Supplier") },
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
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdateName(it)) },
                label = { Text("Supplier Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdatePhone(it)) },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.gst,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdateGst(it)) },
                label = { Text("GST/Tax ID (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.address,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdateAddress(it)) },
                label = { Text("Address (Optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.materialCategories,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdateMaterialCategories(it)) },
                label = { Text("Material Categories (Comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.outstandingBalance,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdateOutstandingBalance(it)) },
                label = { Text("Outstanding Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(SupplierEvent.UpdateNotes(it)) },
                label = { Text("Notes (Optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onEvent(SupplierEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Supplier")
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Supplier") },
            text = { Text("Are you sure you want to delete this supplier?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.onEvent(SupplierEvent.Delete)
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
