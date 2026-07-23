package com.example.ui.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.core.util.DateUtils
import com.example.domain.model.TransactionType
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDashboardScreen(
    viewModel: PaymentDashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = false,
                onNavigationClick = onNavigateBack,
                actions = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
                    Column {
                        Text("Payment Dashboard", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                        Text("Financial Overview & Cash Flow Status", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        OutlinedButton(
                            onClick = { },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Export payments report")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Button(
                            onClick = { },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Create new transaction")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New Payment")
                        }
                    }
                }

                // KPIs - Real Data
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        PaymentKpiCard(
                            modifier = Modifier.weight(1f),
                            label = "RECEIVED",
                            value = CurrencyUtils.formatPaise(uiState.totalReceivedPaise),
                            icon = Icons.Default.AccountBalanceWallet,
                            iconBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                        PaymentKpiCard(
                            modifier = Modifier.weight(1f),
                            label = "PENDING",
                            value = CurrencyUtils.formatPaise(uiState.pendingPaymentsPaise),
                            icon = Icons.Default.PendingActions,
                            iconBg = Color(0xFFFEF3C7),
                            iconTint = Color(0xFFD97706),
                            subtitle = "${uiState.completionPercent}% collected"
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        PaymentKpiCard(
                            modifier = Modifier.weight(1f),
                            label = "EXPENSES",
                            value = CurrencyUtils.formatPaise(uiState.totalExpensesPaise),
                            icon = Icons.Default.TrendingUp,
                            iconBg = Color(0xFFFEE2E2),
                            iconTint = Color(0xFFDC2626)
                        )
                        PaymentKpiCard(
                            modifier = Modifier.weight(1f),
                            label = "NET PROFIT",
                            value = CurrencyUtils.formatPaise(uiState.totalReceivedPaise - uiState.totalExpensesPaise),
                            icon = Icons.Default.CheckCircle,
                            iconBg = Color(0xFFECFDF5),
                            iconTint = Color(0xFF059669)
                        )
                    }
                }

                // Recent Transactions
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium)) {
                            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                            if (uiState.recentTransactions.isEmpty()) {
                                Text(
                                    "No transactions yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                uiState.recentTransactions.forEachIndexed { index, txn ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        if (txn.type == TransactionType.INCOME)
                                                            Color(0xFFECFDF5) else Color(0xFFFEE2E2),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    if (txn.type == TransactionType.INCOME) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                                    contentDescription = if (txn.type == TransactionType.INCOME) "Income" else "Expense",
                                                    tint = if (txn.type == TransactionType.INCOME) Color(0xFF059669) else Color(0xFFDC2626),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                                            Column {
                                                Text(
                                                    txn.description ?: txn.category.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    DateUtils.getRelativeTimeString(txn.date),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            "${if (txn.type == TransactionType.INCOME) "+ Received " else "- Spent "}${CurrencyUtils.formatPaise(txn.amountPaise)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (txn.type == TransactionType.INCOME) Color(0xFF059669) else Color(0xFFDC2626)
                                        )
                                    }
                                    if (index < uiState.recentTransactions.size - 1) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Contract Value Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                            Text("Collection Progress", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            LinearProgressIndicator(
                                progress = { uiState.completionPercent / 100f },
                                modifier = Modifier.fillMaxWidth().height(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Collected: ${CurrencyUtils.formatPaise(uiState.totalReceivedPaise)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Total: ${CurrencyUtils.formatPaise(uiState.totalContractValuePaise)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun PaymentKpiCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    subtitle: String? = null
) {
    Card(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "$label: $value"
        },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
