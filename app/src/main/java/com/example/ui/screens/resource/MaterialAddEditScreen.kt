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
fun MaterialAddEditScreen(
    viewModel: MaterialAddEditViewModel,
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
                title = { Text(if (uiState.isEditing) "Edit Material" else "Add Material") },
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
                onValueChange = { viewModel.onEvent(MaterialEvent.UpdateName(it)) },
                label = { Text("Material Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Category Dropdown
            var expandedCategory by remember { mutableStateOf(false) }
            val categories = listOf("Cement", "Steel", "Sand", "Aggregate", "Bricks", "Blocks", "Tiles", "Paint", "Electrical", "Plumbing", "Wood", "Glass", "Hardware", "Custom")
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = uiState.category,
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
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.onEvent(MaterialEvent.UpdateCategory(cat))
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.unit,
                onValueChange = { viewModel.onEvent(MaterialEvent.UpdateUnit(it)) },
                label = { Text("Unit (e.g. kg, bags, tons)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.currentStock,
                onValueChange = { viewModel.onEvent(MaterialEvent.UpdateCurrentStock(it)) },
                label = { Text("Current Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.minimumStock,
                onValueChange = { viewModel.onEvent(MaterialEvent.UpdateMinimumStock(it)) },
                label = { Text("Minimum Stock Alert Level") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.purchasePrice,
                onValueChange = { viewModel.onEvent(MaterialEvent.UpdatePurchasePrice(it)) },
                label = { Text("Purchase Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.remarks,
                onValueChange = { viewModel.onEvent(MaterialEvent.UpdateRemarks(it)) },
                label = { Text("Remarks (Optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onEvent(MaterialEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Material")
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Material") },
            text = { Text("Are you sure you want to delete this material?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.onEvent(MaterialEvent.Delete)
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
