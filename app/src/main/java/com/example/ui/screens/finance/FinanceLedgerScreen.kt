package com.example.ui.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceLedgerScreen(
    viewModel: FinanceLedgerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (String) -> Unit
) {
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
        ) {
            item {
                Column {
                    Text("Expense Overview", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                    Text("Month-to-Date (MTD)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedButton(
                        onClick = { },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Receipt", color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = onNavigateToAddTransaction,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Expense")
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))))) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("TOTAL SPENT MTD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Box(modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.extraSmall).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("+12%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            Text("$425,000", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("Budget: $500k (85% utilized)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(Dimens.spaceLarge), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 24.dp.toPx()
                                drawArc(color = Color(0xFFF97316), startAngle = -90f, sweepAngle = 144f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Butt))
                                drawArc(color = Color(0xFF515F74), startAngle = 54f, sweepAngle = 126f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Butt))
                                drawArc(color = Color(0xFFB9C7DF), startAngle = 180f, sweepAngle = 54f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Butt))
                                drawArc(color = Color(0xFFE0E3E5), startAngle = 234f, sweepAngle = 36f, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Butt))
                            }
                            Text("Categories", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                                ChartLegendItem(Color(0xFFF97316), "Labor", "40% • $170k", Modifier.weight(1f))
                                ChartLegendItem(Color(0xFF515F74), "Materials", "35% • $148.7k", Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                                ChartLegendItem(Color(0xFFB9C7DF), "Fuel & Transport", "15% • $63.7k", Modifier.weight(1f))
                                ChartLegendItem(Color(0xFFE0E3E5), "Other", "10% • $42.5k", Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.spaceMedium), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("View All", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        
                        TransactionRow(
                            icon = Icons.Default.Construction, iconBg = MaterialTheme.colorScheme.primaryContainer, iconTint = MaterialTheme.colorScheme.primary,
                            title = "Steel Rebar Purchase", subtitle = "Supplier: BuildCo Inc. • Materials",
                            amount = "-$12,450.00", date = "Today, 09:30 AM"
                        )
                        TransactionRow(
                            icon = Icons.Default.LocalGasStation, iconBg = MaterialTheme.colorScheme.secondaryContainer, iconTint = MaterialTheme.colorScheme.secondary,
                            title = "Heavy Machinery Fuel", subtitle = "Site: Alpha Sector • Fuel",
                            amount = "-$3,200.00", date = "Yesterday", showReceipt = true
                        )
                        TransactionRow(
                            icon = Icons.Default.Group, iconBg = MaterialTheme.colorScheme.surfaceContainer, iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            title = "Weekly Contractor Payout", subtitle = "Team A • Labor",
                            amount = "-$45,000.00", date = "Oct 24", isLast = true
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun ChartLegendItem(color: Color, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 4.dp).size(12.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun TransactionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector, iconBg: Color, iconTint: Color,
    title: String, subtitle: String, amount: String, date: String, showReceipt: Boolean = false, isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceMedium), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(40.dp).background(iconBg, CircleShape).border(2.dp, MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(amount, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        if (showReceipt) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Receipt, contentDescription = "Receipt", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
            }
        }
    }
}
