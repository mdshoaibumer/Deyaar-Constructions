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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.core.util.DateUtils
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.domain.usecase.dashboard.DashboardStats
import com.example.ui.components.layout.AnimatedCounter
import com.example.ui.components.layout.ShimmerDashboard
import com.example.ui.theme.Dimens

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToClients: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DashboardUiState.Loading -> ShimmerDashboard()
        is DashboardUiState.Empty -> DashboardEmptyState(
            onCreateClient = onNavigateToClients,
            onCreateProject = onNavigateToProjects
        )
        is DashboardUiState.Success -> DashboardContent(
            stats = state.stats,
            onNavigateToProjects = onNavigateToProjects,
            onNavigateToClients = onNavigateToClients,
            onNavigateToResources = onNavigateToResources,
            onNavigateToReports = onNavigateToReports,
            onNavigateToSettings = onNavigateToSettings
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
            imageVector = Icons.Default.Construction,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(Dimens.spaceLarge))
        Text(
            text = "Welcome to Deyaar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        Text(
            text = "Your business overview will appear here once you add clients and projects.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(Dimens.spaceExtraLarge))
        Button(
            onClick = onCreateClient,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Text("Add First Client")
        }
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        OutlinedButton(
            onClick = onCreateProject,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Text("Create First Project")
        }
    }
}

@Composable
fun DashboardContent(
    stats: DashboardStats,
    onNavigateToProjects: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
    ) {
        item {
            DashboardHeader(onNavigateToSettings = onNavigateToSettings)
        }
        item {
            KpiHorizontalList(stats)
        }
        item {
            FinancialSummary(stats)
        }
        item {
            QuickActionsGrid(
                onNavigateToProjects = onNavigateToProjects,
                onNavigateToClients = onNavigateToClients,
                onNavigateToResources = onNavigateToResources,
                onNavigateToReports = onNavigateToReports
            )
        }
        if (stats.recentProjects.isNotEmpty()) {
            item {
                SectionTitle("Recent Projects")
            }
            items(stats.recentProjects, key = { it.id }) { project ->
                RecentProjectItem(project = project, onClick = { /* Navigate handled at nav level */ })
            }
        }
    }
}

@Composable
fun DashboardHeader(onNavigateToSettings: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = DateUtils.getGreeting(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMicro))
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { heading() }
            )
        }
        IconButton(onClick = onNavigateToSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}

@Composable
fun KpiHorizontalList(stats: DashboardStats) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.spaceMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
    ) {
        item {
            KpiCard(title = "Projects", value = stats.totalProjects, icon = Icons.Default.Folder)
        }
        item {
            KpiCard(title = "Active", value = stats.activeProjects, icon = Icons.Default.HomeRepairService)
        }
        item {
            KpiCard(title = "Completed", value = stats.completedProjects, icon = Icons.Default.CheckCircle)
        }
        item {
            KpiCard(title = "On Hold", value = stats.projectsOnHold, icon = Icons.Default.Pause)
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
        modifier = modifier.width(130.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
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
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                Text(
                    text = "Pending Payments",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Text(
                    text = CurrencyUtils.formatCurrency(stats.pendingPaymentsPaise),
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
                            text = "Received",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = CurrencyUtils.formatCurrency(stats.receivedAmountPaise),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Expenses",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = CurrencyUtils.formatCurrency(stats.totalExpensesPaise),
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
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(vertical = Dimens.spaceSmall)
            .semantics { heading() }
    )
}

@Composable
fun QuickActionsGrid(
    onNavigateToProjects: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToReports: () -> Unit = {}
) {
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
                onClick = onNavigateToProjects
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PersonAdd,
                label = "New Client",
                onClick = onNavigateToClients
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Inventory,
                label = "Resources",
                onClick = onNavigateToResources
            )
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Assessment,
                label = "Reports",
                onClick = onNavigateToReports
            )
        }
    }
}

@Composable
fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(Dimens.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
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
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RecentProjectItem(project: Project, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceMicro)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.shapes.small
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
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = project.status.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${project.progress}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {}
            )
        }
    }
}
