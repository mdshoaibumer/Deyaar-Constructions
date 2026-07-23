package com.example.ui.screens.resource

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialListScreen(
    viewModel: MaterialListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val totalMaterials = uiState.materials.size
    val lowStockCount = uiState.materials.count { it.currentStock <= it.minimumStock }
    val criticalItems = uiState.materials.filter { it.currentStock <= it.minimumStock }.take(5)

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = false,
                onNavigationClick = onNavigateBack,
                actions = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
        ) {
            // Header Stats
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Material Dashboard", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.medium).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), MaterialTheme.shapes.medium).padding(Dimens.spaceMedium)) {
                            Column {
                                Text("TOTAL MATERIALS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("$totalMaterials", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp).padding(bottom = 6.dp))
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), MaterialTheme.shapes.medium).border(1.dp, MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium).padding(Dimens.spaceMedium)) {
                            Column {
                                Text("LOW STOCK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("$lowStockCount", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp).padding(bottom = 6.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        Button(
                            onClick = onNavigateToAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Stock In")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stock In")
                        }
                        OutlinedButton(
                            onClick = { },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Stock Out")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stock Out")
                        }
                    }
                }
            }

            // Critical Inventory Horizontal List
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Critical Inventory", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("VIEW ALL", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        contentPadding = PaddingValues(end = Dimens.marginMobile)
                    ) {
                        item {
                            CriticalInventoryCard(
                                icon = Icons.Default.Construction,
                                iconBgColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                iconTintColor = MaterialTheme.colorScheme.primary,
                                amountLabel = "420 Bags",
                                amountBgColor = MaterialTheme.colorScheme.surfaceContainer,
                                amountTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                title = "Cement",
                                subtitle = "OPC Grade 43",
                                progress = 0.65f,
                                progressColor = MaterialTheme.colorScheme.primary
                            )
                        }
                        item {
                            CriticalInventoryCard(
                                icon = Icons.Default.Architecture,
                                iconBgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                iconTintColor = MaterialTheme.colorScheme.error,
                                amountLabel = "12 Tons",
                                amountBgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                amountTextColor = MaterialTheme.colorScheme.error,
                                title = "Steel Rebar",
                                subtitle = "16mm TMT",
                                progress = 0.15f,
                                progressColor = MaterialTheme.colorScheme.error
                            )
                        }
                        item {
                            CriticalInventoryCard(
                                icon = Icons.Default.Landscape,
                                iconBgColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                iconTintColor = MaterialTheme.colorScheme.primary,
                                amountLabel = "85 Tons",
                                amountBgColor = MaterialTheme.colorScheme.surfaceContainer,
                                amountTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                title = "Coarse Aggregate",
                                subtitle = "20mm Crushed",
                                progress = 0.8f,
                                progressColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Recent Purchases
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = borderStroke(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent Purchases", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        
                        PurchaseRow(Icons.Default.ReceiptLong, "Portland Cement", "PO #8472 • Today", "+200 Bags", "Delivered", MaterialTheme.colorScheme.primary)
                        PurchaseRow(Icons.Default.Architecture, "Steel Rebar 12mm", "PO #8470 • Yesterday", "+5 Tons", "Delivered", MaterialTheme.colorScheme.primary)
                        PurchaseRow(Icons.Default.WaterDrop, "Curing Compound", "PO #8465 • Oct 12", "+50 Liters", "Pending", MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun CriticalInventoryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    amountLabel: String,
    amountBgColor: Color,
    amountTextColor: Color,
    title: String,
    subtitle: String,
    progress: Float,
    progressColor: Color
) {
    Card(
        modifier = Modifier.width(220.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = borderStroke(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.background(iconBgColor, MaterialTheme.shapes.small).padding(8.dp)) {
                    Icon(icon, contentDescription = null, tint = iconTintColor)
                }
                Box(modifier = Modifier.background(amountBgColor, MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(amountLabel, style = MaterialTheme.typography.labelMedium, color = amountTextColor)
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    }
}

@Composable
fun PurchaseRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, amount: String, status: String, statusColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(status, style = MaterialTheme.typography.labelSmall, color = statusColor)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
