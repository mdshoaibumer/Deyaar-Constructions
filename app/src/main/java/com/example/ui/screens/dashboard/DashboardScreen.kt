package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Project
import com.example.domain.usecase.dashboard.DashboardStats
import com.example.ui.components.layout.FullScreenLoading
import com.example.ui.components.layout.AnimatedCounter
import com.example.ui.components.layout.RecentActivityTimeline
import com.example.ui.components.charts.SimpleLineChart
import com.example.ui.theme.Dimens
import com.example.core.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToClients: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToResources: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DashboardUiState.Loading -> FullScreenLoading()
        is DashboardUiState.Empty -> DashboardEmptyState(
            onCreateClient = onNavigateToClients,
            onCreateProject = onNavigateToProjects
        )
        is DashboardUiState.Success -> DashboardContent(
            stats = state.stats,
            onNavigateToResources = onNavigateToResources
        )
    }
}

@Composable
fun DashboardEmptyState(
    onCreateClient: () -> Unit,
    onCreateProject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spaceLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Dashboard,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        Text(
            text = "Welcome to Deyaar Constructions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        Text(
            text = "Your business overview will appear here once you add some data.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.spaceLarge))
        Button(
            onClick = onCreateClient,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Text("Add First Client")
        }
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        OutlinedButton(
            onClick = onCreateProject,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Text("Create First Project")
        }
    }
}

@Composable
fun DashboardContent(
    stats: DashboardStats,
    onNavigateToResources: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dimens.spaceExtraLarge)
    ) {
        item {
            DashboardHeader()
        }
        item {
            KpiHorizontalList(stats)
        }
        item {
            FinancialSummary(stats)
        }
        item {
            ExpenseTrendChart()
        }
        item {
            QuickActionsGrid(onNavigateToResources = onNavigateToResources)
        }
        item {
            MaterialSummaryWidget()
        }
        item {
            UpcomingItemsWidget()
        }
        item {
            RecentActivityTimeline()
        }
        if (stats.recentProjects.isNotEmpty()) {
            item {
                SectionTitle("Recent Projects")
            }
            items(stats.recentProjects, key = { it.id }) { project ->
                RecentProjectItem(project)
            }
        }
    }
}

@Composable
fun DashboardHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceMedium)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Business Overview",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun KpiHorizontalList(stats: DashboardStats) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.spaceMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
    ) {
        item {
            KpiCard(title = "Active Projects", value = stats.activeProjects, icon = Icons.Default.HomeRepairService)
        }
        item {
            KpiCard(title = "Total Clients", value = stats.totalClients, icon = Icons.Default.People)
        }
        item {
            KpiCard(title = "Completed", value = stats.completedProjects, icon = Icons.Default.CheckCircle)
        }
        item {
            KpiCard(title = "Labour Today", value = stats.todaysLabourCount, icon = Icons.Default.Engineering)
        }
    }
    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    icon: ImageVector
) {
    Card(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(Dimens.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            AnimatedCounter(
                count = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMicro))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FinancialSummary(stats: DashboardStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceMedium)
    ) {
        SectionTitle("Financial Overview")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                Text(
                    text = "Total Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Text(
                    text = CurrencyUtils.formatCurrency(stats.totalExpensesPaise),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Contract Value",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = CurrencyUtils.formatCurrency(stats.totalContractValuePaise),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Net Profit",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = CurrencyUtils.formatCurrency(stats.netProfitPaise),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseTrendChart() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium)
    ) {
        SectionTitle("Monthly Expenses Trend")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(Dimens.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.spaceMedium)
            ) {
                // Mock data points
                val data = listOf(10f, 30f, 25f, 50f, 40f, 70f, 60f, 90f)
                SimpleLineChart(
                    dataPoints = data,
                    lineColor = MaterialTheme.colorScheme.primary,
                    lineWidth = 8f
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            vertical = Dimens.spaceSmall
        )
    )
}

@Composable
fun QuickActionsGrid(onNavigateToResources: () -> Unit) {
    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
        SectionTitle("Quick Actions")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AddBusiness,
                label = "New Project",
                onClick = { /* TODO */ }
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PersonAdd,
                label = "New Client",
                onClick = { /* TODO */ }
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Inventory,
                label = "Resources",
                onClick = onNavigateToResources
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Payment,
                label = "Payment",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(Dimens.radiusMedium))
            .clickable { onClick() }
            .padding(Dimens.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(Dimens.radiusMedium)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MaterialSummaryWidget() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceMedium)
    ) {
        SectionTitle("Material Summary")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spaceMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                RoundedCornerShape(Dimens.radiusSmall)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                    Column {
                        Text(
                            text = "Low Stock Alert",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cement, Steel Rods (10mm)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun UpcomingItemsWidget() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium)
    ) {
        SectionTitle("Action Required")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                ActionItemRow(icon = Icons.Default.Payment, title = "2 Pending Payments", subtitle = "To subcontractors")
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                ActionItemRow(icon = Icons.Default.DateRange, title = "Deadline Approaching", subtitle = "Al Barsha Villa (3 Days)")
            }
        }
    }
}

@Composable
fun ActionItemRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentProjectItem(project: Project) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall)
            .clickable { /* TODO */ },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(Dimens.radiusLarge),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(Dimens.radiusMedium)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = project.location ?: "Unknown Location",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            if (project.status.name == "COMPLETED") MaterialTheme.colorScheme.tertiaryContainer 
                            else MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(Dimens.radiusSmall)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = project.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (project.status.name == "COMPLETED") MaterialTheme.colorScheme.onTertiaryContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            
            // Progress Bar (Mocked to 60%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.spaceSmall)
                        .clip(RoundedCornerShape(Dimens.radiusSmall)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                Text(
                    text = "60%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
