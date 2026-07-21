package com.example.ui.screens.client

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.ClientCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientAddEditScreen(
    viewModel: ClientAddEditViewModel,
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
                title = { Text(if (uiState.name.isBlank()) "Add Client" else "Edit Client") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(ClientAddEditEvent.Save) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("save_client_fab")
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimens.spaceMedium)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = Dimens.spaceSmall)
                    )
                }

                Text("Basic Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.NameChanged(it)) },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                OutlinedTextField(
                    value = uiState.companyName,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.CompanyNameChanged(it)) },
                    label = { Text("Company Name (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.PhoneChanged(it)) },
                    label = { Text("Mobile Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                
                OutlinedTextField(
                    value = uiState.whatsapp,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.WhatsappChanged(it)) },
                    label = { Text("WhatsApp Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.EmailChanged(it)) },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.spaceSmall))
                Text("Address Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.AddressChanged(it)) },
                    label = { Text("Complete Address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                    OutlinedTextField(
                        value = uiState.city,
                        onValueChange = { viewModel.onEvent(ClientAddEditEvent.CityChanged(it)) },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.state,
                        onValueChange = { viewModel.onEvent(ClientAddEditEvent.StateChanged(it)) },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.spaceSmall))
                Text("Business Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = uiState.gstNumber,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.GstChanged(it)) },
                    label = { Text("GST Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.onEvent(ClientAddEditEvent.NotesChanged(it)) },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}
