package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.core.util.DateUtils
import com.example.domain.model.Project
import com.example.domain.usecase.dashboard.DashboardStats
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.components.layout.ShimmerDashboard
import com.example.ui.theme.Dimens
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToClients: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToReports: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSitePhotos: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DashboardUiState.Loading -> ShimmerDashboard()
        is DashboardUiState.Empty -> DashboardContent(
            stats = DashboardStats(),
            onNavigateToProjects = onNavigateToProjects,
            onNavigateToClients = onNavigateToClients,
            onNavigateToResources = onNavigateToResources,
            onNavigateToReports = onNavigateToReports,
            onNavigateToPayments = onNavigateToPayments,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToSitePhotos = onNavigateToSitePhotos
        )
        is DashboardUiState.Success -> DashboardContent(
            stats = state.stats,
            onNavigateToProjects = onNavigateToProjects,
            onNavigateToClients = onNavigateToClients,
            onNavigateToResources = onNavigateToResources,
            onNavigateToReports = onNavigateToReports,
            onNavigateToPayments = onNavigateToPayments,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToSitePhotos = onNavigateToSitePhotos
        )
    }
}

@Composable
fun DashboardContent(
    stats: DashboardStats,
    onNavigateToProjects: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToReports: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSitePhotos: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search projects, clients, and workers",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToProjects,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new project")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
        ) {
            item {
                GreetingSection(name = "Alex")
            }

            item {
                BentoGridStats(stats)
            }

            item {
                MonthlyExpensesChart(monthlyExpensesPaise = stats.monthlyExpensesPaise)
            }

            item {
                QuickActions(
                    onProject = onNavigateToProjects,
                    onClient = onNavigateToClients,
                    onExpense = onNavigateToPayments,
                    onSitePhoto = onNavigateToSitePhotos
                )
            }

            item {
                RecentExpensesSection(recentExpenses = stats.recentExpenses)
            }

            item {
                UpcomingDeadlines(deadlines = stats.upcomingDeadlines)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun GreetingSection(name: String = "Alex") {
    val greeting = DateUtils.getGreeting()
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "$greeting, $name. Here is what's happening on your sites today."
        }
    ) {
        Text(
            text = "$greeting, $name",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Here is what's happening on your sites today.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BentoGridStats(stats: DashboardStats) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            modifier = Modifier.fillMaxWidth()
        ) {
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Total Projects",
                value = stats.totalProjects.toString(),
                icon = Icons.Default.Architecture,
                badgeText = "ALL",
                badgeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                badgeTextColor = MaterialTheme.colorScheme.primary
            )
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Active",
                value = stats.activeProjects.toString(),
                icon = Icons.Default.Bolt,
                badgeText = "LIVE",
                badgeColor = MaterialTheme.colorScheme.primaryContainer,
                badgeTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            modifier = Modifier.fillMaxWidth()
        ) {
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Completed",
                value = stats.completedProjects.toString(),
                icon = Icons.Default.TaskAlt
            )
            BentoCard(
                modifier = Modifier.weight(1f),
                title = "Pending",
                value = CurrencyUtils.formatPaise(stats.pendingPaymentsPaise),
                icon = Icons.Default.Payments,
                isAlert = stats.pendingPaymentsPaise > 0
            )
        }
    }
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    badgeText: String? = null,
    badgeColor: Color = Color.Transparent,
    badgeTextColor: Color = Color.Transparent,
    isAlert: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.large
            )
            .clickable { }
            .padding(Dimens.spaceMedium)
            .semantics(mergeDescendants = true) {
                contentDescription = "$title: $value"
                role = Role.Button
            }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Covered by parent semantics
                    tint = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .background(badgeColor, MaterialTheme.shapes.extraSmall)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeTextColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MonthlyExpensesChart(monthlyExpensesPaise: List<Long>) {
    // Convert paise to lakhs for readable chart display
    val chartData = if (monthlyExpensesPaise.isNotEmpty()) {
        monthlyExpensesPaise.map { it.toFloat() / 100_000f } // Convert paise to thousands (INR)
    } else {
        listOf(0f, 0f, 0f, 0f, 0f, 0f) // Empty state
    }
    val chartEntryModel = entryModelOf(*chartData.map { it as Number }.toTypedArray())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.large)
            .padding(Dimens.spaceLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Monthly Expenses (₹ in thousands)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Last 6 months",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(Dimens.spaceLarge))

        if (chartData.any { it > 0f }) {
            Chart(
                chart = lineChart(),
                model = chartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                modifier = Modifier
                    .height(150.dp)
                    .semantics { contentDescription = "Monthly expenses line chart showing last 6 months of spending. " +
                        monthlyExpensesPaise.mapIndexed { i, v -> "Month ${i + 1}: ${CurrencyUtils.formatPaise(v)}" }.joinToString(". ")
                    }
            )
            // Accessible data summary for screen readers
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalExpenses = monthlyExpensesPaise.sum()
                Text(
                    text = "Total: ${CurrencyUtils.formatPaise(totalExpenses)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val avgExpenses = if (monthlyExpensesPaise.isNotEmpty()) totalExpenses / monthlyExpensesPaise.size else 0L
                Text(
                    text = "Avg: ${CurrencyUtils.formatPaise(avgExpenses)}/month",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No expense data available yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecentExpensesSection(recentExpenses: List<Pair<String, Long>>) {
    if (recentExpenses.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.large)
            .padding(Dimens.spaceLarge)
    ) {
        Text(
            text = "Recent Expenses",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

        recentExpenses.forEach { (description, amountPaise) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.spaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = description.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = CurrencyUtils.formatPaise(amountPaise),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (recentExpenses.last().first != description) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActions(
    onProject: () -> Unit,
    onClient: () -> Unit,
    onExpense: () -> Unit,
    onSitePhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(Dimens.spaceLarge)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionButton(
                icon = Icons.Default.AddCircle,
                label = "Project",
                onClick = onProject,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.PersonAdd,
                label = "Client",
                onClick = onClient,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.ReceiptLong,
                label = "Expense",
                onClick = onExpense,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.AddAPhoto,
                label = "Site Photo",
                onClick = onSitePhoto,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick, onClickLabel = "Open $label")
            .padding(Dimens.spaceMedium)
            .semantics { role = Role.Button }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Label provides description
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun UpcomingDeadlines(deadlines: List<Project>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(Dimens.spaceLarge)
    ) {
        Text(
            text = "Upcoming Deadlines",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

        if (deadlines.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(Dimens.spaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                Text(
                    text = "No upcoming deadlines. All caught up!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            deadlines.forEachIndexed { index, project ->
                val daysUntilDeadline = project.expectedCompletionDate?.let {
                    ((it - System.currentTimeMillis()) / 86_400_000L).toInt()
                } ?: 0
                val isUrgent = daysUntilDeadline <= 7

                DeadlineItem(
                    title = project.name,
                    subtitle = when {
                        daysUntilDeadline <= 0 -> "Overdue"
                        daysUntilDeadline == 1 -> "Due Tomorrow"
                        daysUntilDeadline <= 7 -> "Due in $daysUntilDeadline days"
                        else -> "Due ${DateUtils.formatToDate(project.expectedCompletionDate ?: 0L)}"
                    },
                    icon = if (isUrgent) Icons.Default.Warning else Icons.Default.CalendarToday,
                    iconColor = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    isAlert = isUrgent
                )
                if (index < deadlines.size - 1) {
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                }
            }
        }
    }
}

@Composable
fun DeadlineItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    isAlert: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isAlert) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(Dimens.spaceMedium)
            .semantics(mergeDescendants = true) {
                contentDescription = "$title, $subtitle"
            },
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isAlert) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
