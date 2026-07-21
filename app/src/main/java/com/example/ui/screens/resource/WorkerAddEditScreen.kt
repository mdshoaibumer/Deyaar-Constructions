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
fun WorkerAddEditScreen(
    viewModel: WorkerAddEditViewModel,
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
                title = { Text(if (uiState.isEditing) "Edit Worker" else "Add Worker") },
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
                value = uiState.fullName,
                onValueChange = { viewModel.onEvent(WorkerEvent.UpdateFullName(it)) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.mobileNumber,
                onValueChange = { viewModel.onEvent(WorkerEvent.UpdateMobileNumber(it)) },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            // Trade Dropdown
            var expandedTrade by remember { mutableStateOf(false) }
            val trades = listOf("Mason", "Helper", "Carpenter", "Steel Fixer", "Electrician", "Plumber", "Painter", "Tile Worker", "Welder", "Machine Operator", "Supervisor", "Custom")
            ExposedDropdownMenuBox(
                expanded = expandedTrade,
                onExpandedChange = { expandedTrade = !expandedTrade }
            ) {
                OutlinedTextField(
                    value = uiState.trade,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Trade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTrade) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTrade,
                    onDismissRequest = { expandedTrade = false }
                ) {
                    trades.forEach { trade ->
                        DropdownMenuItem(
                            text = { Text(trade) },
                            onClick = {
                                viewModel.onEvent(WorkerEvent.UpdateTrade(trade))
                                expandedTrade = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.dailyWage,
                onValueChange = { viewModel.onEvent(WorkerEvent.UpdateDailyWage(it)) },
                label = { Text("Daily Wage") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.experience,
                onValueChange = { viewModel.onEvent(WorkerEvent.UpdateExperience(it)) },
                label = { Text("Experience (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.emergencyContact,
                onValueChange = { viewModel.onEvent(WorkerEvent.UpdateEmergencyContact(it)) },
                label = { Text("Emergency Contact (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.address,
                onValueChange = { viewModel.onEvent(WorkerEvent.UpdateAddress(it)) },
                label = { Text("Address (Optional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onEvent(WorkerEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Worker")
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Worker") },
            text = { Text("Are you sure you want to delete this worker?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.onEvent(WorkerEvent.Delete)
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
