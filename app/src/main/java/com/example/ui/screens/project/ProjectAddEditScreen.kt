package com.example.ui.screens.project

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectAddEditScreen(
    viewModel: ProjectAddEditViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.projectNumber.isNotBlank()) "Edit Project" else "Add Project") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { viewModel.onEvent(ProjectAddEditEvent.Save) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spaceMedium)
                ) {
                    Text("Save Project")
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = uiState.projectNumber,
                    onValueChange = {},
                    label = { Text("Project Number") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onEvent(ProjectAddEditEvent.NameChanged(it)) },
                    label = { Text("Project Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Client Selection (Simplified for now using a basic dropdown or just showing IDs)
                var clientDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = clientDropdownExpanded,
                    onExpandedChange = { clientDropdownExpanded = it }
                ) {
                    val selectedClient = uiState.clients.find { it.id == uiState.clientId }
                    OutlinedTextField(
                        value = selectedClient?.name ?: "Select Client *",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = clientDropdownExpanded,
                        onDismissRequest = { clientDropdownExpanded = false }
                    ) {
                        uiState.clients.forEach { client ->
                            DropdownMenuItem(
                                text = { Text(client.name) },
                                onClick = {
                                    viewModel.onEvent(ProjectAddEditEvent.ClientSelected(client.id))
                                    clientDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Selection
                var categoryDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.category.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ProjectCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    viewModel.onEvent(ProjectAddEditEvent.CategoryChanged(category))
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.contractValue,
                    onValueChange = { viewModel.onEvent(ProjectAddEditEvent.ContractValueChanged(it)) },
                    label = { Text("Contract Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = uiState.estimatedBudget,
                    onValueChange = { viewModel.onEvent(ProjectAddEditEvent.BudgetChanged(it)) },
                    label = { Text("Estimated Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Simplified status
                var statusDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = statusDropdownExpanded,
                    onExpandedChange = { statusDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.status.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false }
                    ) {
                        ProjectStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName) },
                                onClick = {
                                    viewModel.onEvent(ProjectAddEditEvent.StatusChanged(status))
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.progress,
                    onValueChange = { viewModel.onEvent(ProjectAddEditEvent.ProgressChanged(it)) },
                    label = { Text("Progress %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.onEvent(ProjectAddEditEvent.NotesChanged(it)) },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }
    }
}
