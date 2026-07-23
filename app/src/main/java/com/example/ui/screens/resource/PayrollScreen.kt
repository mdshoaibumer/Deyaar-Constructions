package com.example.ui.screens.resource

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
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
fun PayrollScreen(
    viewModel: PayrollViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                onNavigationClick = onNavigateBack,
                actions = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.ShimmerCardList(modifier = Modifier.padding(paddingValues))
        } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Payroll Management", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                        Text("DEYAAR CONSTRUCTIONS • ${uiState.weekLabel}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = "Process Payment", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Process Bulk Payment", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = borderStroke(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = "Payroll Summary", tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Payroll Summary", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                            Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small).padding(Dimens.spaceMedium)) {
                                Column {
                                    Text("TOTAL PENDING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        com.example.core.util.CurrencyUtils.formatPaise(uiState.totalPendingPaise),
                                        style = MaterialTheme.typography.displaySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Box(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small).padding(Dimens.spaceMedium)) {
                                Column {
                                    Text("TOTAL PAID (MTD)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        com.example.core.util.CurrencyUtils.formatPaise(uiState.totalPaidMtdPaise),
                                        style = MaterialTheme.typography.displaySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("All prior obligations met", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1C2E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = "Week Breakdown", tint = Color(0xFF909BB1))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${uiState.weekLabel} Breakdown", style = MaterialTheme.typography.titleMedium, color = Color(0xFFD5E3FC))
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                        BreakdownRow("Standard Hours", "${String.format("%.0f", uiState.standardHours)} hrs")
                        BreakdownRow("Overtime (1.5x)", "${String.format("%.0f", uiState.overtimeHours)} hrs", true)
                        BreakdownRow("Workers on Payroll", "${uiState.workerPayrollItems.size}")
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        HorizontalDivider(color = Color(0xFF3C475A))
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Text("Projected Payout", style = MaterialTheme.typography.titleMedium, color = Color(0xFFD5E3FC))
                            Text(com.example.core.util.CurrencyUtils.formatPaise(uiState.totalPendingPaise), style = MaterialTheme.typography.displaySmall, color = Color(0xFFFFB690))
                        }
                    }
                }
            }

            if (uiState.workerPayrollItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceExtraLarge), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                                Text("No payroll data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Add workers and mark attendance to generate payroll.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = borderStroke(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow).padding(Dimens.spaceMedium)) {
                                Text("Weekly Wages (${uiState.workerPayrollItems.count { it.status == PayrollStatus.PENDING }} Pending)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        }
                    }
                }

                items(uiState.workerPayrollItems) { item ->
                    val statusColor = when (item.status) {
                        PayrollStatus.PAID -> MaterialTheme.colorScheme.secondary
                        PayrollStatus.PENDING -> Color(0xFF909BB1)
                        PayrollStatus.FLAGGED -> Color(0xFFBA1A1A)
                    }
                    PayrollWorkerRow(
                        item.initials,
                        item.name,
                        item.id,
                        item.trade,
                        String.format("%.1f", item.hoursWorked),
                        com.example.core.util.CurrencyUtils.formatPaise(item.amountPaise),
                        item.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        statusColor
                    )
                }
            }
        }
        }
    }
}

@Composable
fun BreakdownRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.spaceSmall), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB9C7DF))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = if (isHighlight) Color(0xFFFFB690) else Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PayrollWorkerRow(initials: String, name: String, id: String, trade: String, hours: String, amount: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Text(initials, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Column {
                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(id, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
        Text(trade, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Text(hours, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(amount, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
        Box(modifier = Modifier.background(statusColor.copy(alpha = 0.1f), MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(status, style = MaterialTheme.typography.labelSmall, color = statusColor)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
