package com.example.ui.screens.finance

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceLedgerScreen(
    viewModel: FinanceLedgerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Ledger") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                // Dashboard Section
                uiState.analysis?.let { analysis ->
                    item {
                        DashboardCards(analysis)
                    }
                }

                item {
                    Text("Transactions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                if (uiState.filteredTransactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No transactions found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    // Group by date
                    val grouped = uiState.filteredTransactions.groupBy { 
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it.date)) 
                    }
                    
                    grouped.forEach { (dateStr, transactions) ->
                        item {
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(transactions, key = { it.id }) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onClick = { onNavigateToEditTransaction(transaction.id) }
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(64.dp)) }
            }
        }
    }
}

@Composable
fun DashboardCards(analysis: FinanceAnalysisData) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
        // Net Profit Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                Text("Net Balance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = CurrencyUtils.formatPaise(analysis.netProfitPaise),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Row for Income / Expense
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Dimens.spaceMicro))
                        Text("Income", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = CurrencyUtils.formatPaise(analysis.monthIncomePaise),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("This Month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Dimens.spaceMicro))
                        Text("Expense", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = CurrencyUtils.formatPaise(analysis.monthExpensePaise),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("This Month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        // Project Cost Analysis
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                Text("Project Cost Analysis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Material Cost", style = MaterialTheme.typography.bodyMedium)
                    Text(CurrencyUtils.formatPaise(analysis.materialCostPaise), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(Dimens.spaceMicro))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Labour Cost", style = MaterialTheme.typography.bodyMedium)
                    Text(CurrencyUtils.formatPaise(analysis.labourCostPaise), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(Dimens.spaceMicro))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Other Expenses", style = MaterialTheme.typography.bodyMedium)
                    Text(CurrencyUtils.formatPaise(analysis.otherExpensesPaise), fontWeight = FontWeight.SemiBold)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.spaceSmall))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Cost", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(CurrencyUtils.formatPaise(analysis.materialCostPaise + analysis.labourCostPaise + analysis.otherExpensesPaise), fontWeight = FontWeight.Bold)
                }
                
                if (analysis.estimatedProfitPaise != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Profit", style = MaterialTheme.typography.bodyMedium)
                        Text(CurrencyUtils.formatPaise(analysis.estimatedProfitPaise), fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                    }
                }
                if (analysis.profitMarginPercent != null) {
                    Spacer(modifier = Modifier.height(Dimens.spaceMicro))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Profit Margin", style = MaterialTheme.typography.bodyMedium)
                        Text(String.format(Locale.getDefault(), "%.1f%%", analysis.profitMarginPercent), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val sign = if (isIncome) "+" else "-"
    val icon = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isIncome) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = amountColor)
            }
            
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (!transaction.description.isNullOrBlank()) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    text = transaction.paymentMethod.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "$sign${CurrencyUtils.formatPaise(transaction.amountPaise)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
