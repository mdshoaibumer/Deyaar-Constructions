package com.example.ui.screens.client

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Client
import com.example.domain.usecase.client.GetClientByIdUseCase
import com.example.domain.usecase.client.DeleteClientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientDetailsViewModel(
    private val clientId: String,
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val deleteClientUseCase: DeleteClientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientDetailsUiState(isLoading = true))
    val uiState: StateFlow<ClientDetailsUiState> = _uiState.asStateFlow()

    init {
        loadClient()
    }

    fun loadClient() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val client = getClientByIdUseCase(clientId)
            if (client != null) {
                _uiState.update { it.copy(client = client, isLoading = false, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Client not found") }
            }
        }
    }

    fun deleteClient(onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteClientUseCase(clientId)
            onSuccess()
        }
    }
}

data class ClientDetailsUiState(
    val client: Client? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ClientDetailsViewModelFactory(
    private val clientId: String,
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val deleteClientUseCase: DeleteClientUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientDetailsViewModel(clientId, getClientByIdUseCase, deleteClientUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(
    viewModel: ClientDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditClient: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Client") },
            text = { Text("Are you sure you want to delete this client? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteClient(onSuccess = onNavigateBack)
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Client Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    uiState.client?.let { client ->
                        IconButton(onClick = { onNavigateToEditClient(client.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.FullScreenLoading(modifier = Modifier.padding(paddingValues))
        } else if (uiState.client != null) {
            val client = uiState.client!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(Dimens.spaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = client.name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (!client.companyName.isNullOrBlank()) {
                        Text(
                            text = client.companyName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.spaceMedium),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAction(icon = Icons.Outlined.Phone, label = "Call") {}
                    QuickAction(icon = Icons.AutoMirrored.Filled.Chat, label = "WhatsApp") {}
                    QuickAction(icon = Icons.Outlined.Email, label = "Email") {}
                    QuickAction(icon = Icons.Outlined.LocationOn, label = "Maps") {}
                }

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                
                // Details Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.spaceMedium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                        Text("Contact Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        DetailItem(icon = Icons.Outlined.Phone, label = "Mobile", value = client.phone)
                        if (!client.whatsapp.isNullOrBlank()) {
                            DetailItem(icon = Icons.AutoMirrored.Filled.Chat, label = "WhatsApp", value = client.whatsapp)
                        }
                        if (!client.email.isNullOrBlank()) {
                            DetailItem(icon = Icons.Outlined.Email, label = "Email", value = client.email)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.spaceMedium))
                        Text("Address", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        val fullAddress = listOfNotNull(client.address, client.city, client.state, client.pincode).joinToString(", ")
                        DetailItem(icon = Icons.Outlined.LocationOn, label = "Address", value = fullAddress.takeIf { it.isNotBlank() } ?: "N/A")
                    }
                }

                // Financial Summary Card
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.spaceMedium),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                        Text("Financial Summary", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        // These values would ideally come from a use case that aggregates project data
                        // For now showing the client notes which may contain contract references
                        DetailItem(icon = Icons.Outlined.LocationOn, label = "Project Location", value = client.address ?: client.city ?: "See linked projects")
                        if (!client.notes.isNullOrBlank()) {
                            DetailItem(icon = Icons.Outlined.Phone, label = "Notes", value = client.notes)
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        Text(
                            "View linked projects for contract values and payment details.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Client not found")
            }
        }
    }
}

@Composable
fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(Dimens.spaceSmall)) {
        FilledIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(icon, contentDescription = label)
        }
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
