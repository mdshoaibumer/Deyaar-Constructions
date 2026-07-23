package com.example.ui.screens.resource

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Material
import com.example.domain.model.ResourceAllocation
import com.example.domain.model.ResourceType
import com.example.domain.repository.ResourceRepository
import com.example.ui.theme.Dimens
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MaterialUsageUiState(
    val materials: List<Material> = emptyList(),
    val allocations: List<ResourceAllocation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class MaterialUsageViewModel(
    private val materialId: String?,
    private val repository: ResourceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MaterialUsageUiState())
    val uiState: StateFlow<MaterialUsageUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAllMaterials().collect { materials ->
                _uiState.update { it.copy(materials = materials, isLoading = false) }
            }
        }
        if (materialId != null) {
            viewModelScope.launch {
                repository.getAllocationsForResource(materialId).collect { allocs ->
                    _uiState.update { it.copy(allocations = allocs) }
                }
            }
        }
    }

    fun recordUsage(materialId: String, projectId: String, quantity: Double, remarks: String?) {
        viewModelScope.launch {
            try {
                val material = repository.getMaterialById(materialId)
                val allocation = ResourceAllocation(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    date = System.currentTimeMillis(),
                    resourceType = ResourceType.MATERIAL,
                    resourceId = materialId,
                    quantity = quantity,
                    hours = null,
                    costPaise = ((material?.purchasePricePaise ?: 0L) * quantity).toLong(),
                    remarks = remarks,
                    siteDiaryId = null,
                    transactionId = null,
                    createdAt = System.currentTimeMillis()
                )
                repository.saveAllocation(allocation)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

class MaterialUsageViewModelFactory(
    private val materialId: String?,
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MaterialUsageViewModel(materialId, repository) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialUsageScreen(
    viewModel: MaterialUsageViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        RecordUsageDialog(
            materials = uiState.materials,
            onDismiss = { showAddDialog = false },
            onConfirm = { matId, qty, remarks ->
                // Use a default project for now
                viewModel.recordUsage(matId, "default", qty, remarks)
                showAddDialog = false
                Toast.makeText(context, "Usage recorded. Stock updated.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material Usage") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Record Usage")
            }
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
                item {
                    Text("Current Stock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                }

                items(uiState.materials, key = { it.id }) { material ->
                    MaterialStockCard(material = material)
                }

                if (uiState.allocations.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        Text("Usage History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(uiState.allocations, key = { it.id }) { allocation ->
                        AllocationHistoryItem(allocation = allocation, materials = uiState.materials)
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialStockCard(material: Material) {
    val isLow = material.currentStock <= material.minimumStock
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isLow) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(material.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("${material.category} | Min: ${material.minimumStock} ${material.unit}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${material.currentStock} ${material.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                if (isLow) Text("LOW STOCK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AllocationHistoryItem(allocation: ResourceAllocation, materials: List<Material>) {
    val material = materials.find { it.id == allocation.resourceId }
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(allocation.date))
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(Dimens.spaceMedium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(material?.name ?: "Material", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (!allocation.remarks.isNullOrBlank()) {
                    Text(allocation.remarks, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("-${allocation.quantity} ${material?.unit ?: ""}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordUsageDialog(
    materials: List<Material>,
    onDismiss: () -> Unit,
    onConfirm: (materialId: String, quantity: Double, remarks: String?) -> Unit
) {
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }
    var quantity by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Material Usage") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedMaterial?.name ?: "Select Material",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        materials.forEach { mat ->
                            DropdownMenuItem(
                                text = { Text("${mat.name} (${mat.currentStock} ${mat.unit})") },
                                onClick = { selectedMaterial = mat; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity Used") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val mat = selectedMaterial
                val qty = quantity.toDoubleOrNull()
                if (mat != null && qty != null && qty > 0) {
                    onConfirm(mat.id, qty, remarks.takeIf { it.isNotBlank() })
                }
            }) { Text("Record") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
