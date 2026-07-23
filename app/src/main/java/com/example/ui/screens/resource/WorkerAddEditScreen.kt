package com.example.ui.screens.resource

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.components.DeyaarTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerAddEditScreen(
    viewModel: WorkerAddEditViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    // If we are editing an existing worker, we show the Profile View first.
    var showEditForm by remember { mutableStateOf(!uiState.isEditing) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "Worker Profile",
                showLogo = false,
                onNavigationClick = onNavigateBack,
                actions = {
                    if (uiState.isEditing && !showEditForm) {
                        IconButton(onClick = { showEditForm = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                    if (uiState.isEditing && showEditForm) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (showEditForm) {
                // ADD/EDIT FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.spaceMedium)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
                ) {
                    Text("Worker Details", style = MaterialTheme.typography.titleLarge)
                    
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

                    if (uiState.error != null) {
                        Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        if (uiState.isEditing) {
                            OutlinedButton(
                                onClick = { showEditForm = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }
                        Button(
                            onClick = { viewModel.onEvent(WorkerEvent.Save) },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isSaving
                        ) {
                            Text(if (uiState.isSaving) "Saving..." else "Save Worker")
                        }
                    }
                }
            } else {
                // PROFILE VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.marginMobile)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
                ) {
                    // Profile Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .border(4.dp, MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(uiState.fullName.take(1).uppercase(), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column {
                                    Text(uiState.fullName, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("ID: DY-84920", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Active Worker", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Call")
                                }
                                OutlinedButton(
                                    onClick = { },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Message")
                                }
                            }
                        }
                    }

                    // Stats Grid
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        StatCard("Joining Date", "Oct 2023", Icons.Default.CalendarToday, Modifier.weight(1f))
                        StatCard("Daily Wage", "₹${uiState.dailyWage}", Icons.Default.Paid, Modifier.weight(1f))
                        StatCard("Total Paid", "₹12,400", Icons.Default.AccountBalanceWallet, Modifier.weight(1f), true)
                    }

                    // Assigned Project & Skills
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Text("Assigned Project", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                                Box(
                                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Architecture, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Column {
                                    Text("Skyline Residency", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Block C, Level 4", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            
                            Text("Skills & Trade", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.surfaceContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(uiState.trade, style = MaterialTheme.typography.bodySmall)
                                }
                                Box(modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.surfaceContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("General", style = MaterialTheme.typography.bodySmall)
                                }
                                Box(modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.secondaryContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Safety Certified", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Attendance Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Attendance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = null) }
                            }
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            
                            // Radial Progress
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(progress = { 1f }, color = MaterialTheme.colorScheme.surfaceContainerHighest, strokeWidth = 12.dp, modifier = Modifier.fillMaxSize())
                                    CircularProgressIndicator(progress = { 0.94f }, color = MaterialTheme.colorScheme.primary, strokeWidth = 12.dp, modifier = Modifier.fillMaxSize(), strokeCap = androidx.compose.ui.graphics.StrokeCap.Round)
                                    Text("94%", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                                Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small).padding(Dimens.spaceMedium), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("24", style = MaterialTheme.typography.titleMedium)
                                        Text("Days Present", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                                Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small).padding(Dimens.spaceMedium), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("2", style = MaterialTheme.typography.titleMedium)
                                        Text("Days Absent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
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

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, isPrimary: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Text(value, style = MaterialTheme.typography.titleMedium, color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }
    }
}
