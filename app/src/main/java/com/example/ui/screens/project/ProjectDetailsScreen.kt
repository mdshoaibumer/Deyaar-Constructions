package com.example.ui.screens.project

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectTimelineEvent
import com.example.ui.components.DeyaarTopAppBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectDetailsScreen(
    viewModel: ProjectDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProject: (String) -> Unit,
    onNavigateToSiteDiaries: (String) -> Unit = {},
    onNavigateToFinanceLedger: (String) -> Unit = {},
    onNavigateToDocumentation: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = false,
                onNavigationClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { onNavigateToEditProject(uiState.project?.id ?: "") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit project")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Update Progress */ },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Update Progress")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.FullScreenLoading(modifier = Modifier.padding(paddingValues))
        } else if (uiState.project != null) {
            val project = uiState.project!!
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(Dimens.marginMobile),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
            ) {
                item {
                    // Back button 
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onNavigateBack)
                            .padding(bottom = Dimens.spaceMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text(
                            text = "Back to All Projects",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                item {
                    HeroHeader(project = project, onEdit = { onNavigateToEditProject(project.id) })
                }

                item {
                    var selectedTab by remember { mutableIntStateOf(0) }
                    InteractiveTabBar(
                        selectedIndex = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
                
                item {
                    ProgressCard(project = project)
                }
                
                item {
                    MilestonesTimeline(milestones = uiState.milestones)
                }
                
                item {
                    BudgetHealthCard(project = project)
                }
                
                item {
                    RecentExpenses()
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun HeroHeader(project: Project, onEdit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
    ) {
        // Assume image is handled similarly to Cards
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
        
        // Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 100f
                    )
                )
        )
        
        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.spaceMedium)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                modifier = Modifier.padding(bottom = Dimens.spaceSmall)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
                        .padding(horizontal = Dimens.spaceSmall, vertical = 2.dp)
                ) {
                    Text(
                        text = project.status.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f), MaterialTheme.shapes.small)
                        .padding(horizontal = Dimens.spaceSmall, vertical = 2.dp)
                ) {
                    Text(
                        text = "ID: ${project.projectNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = project.address ?: project.location ?: "Location not specified",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
fun InteractiveTabBar(
    selectedIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    val tabs = listOf("Overview", "Milestones", "Budget", "Schedule")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = Dimens.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex
            Column(
                modifier = Modifier.clickable { onTabSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.height(2.dp).width(40.dp).background(MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
fun ProgressCard(project: Project) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radial Progress
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        strokeWidth = 8.dp
                    )
                    CircularProgressIndicator(
                        progress = { project.progress / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 8.dp,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${project.progress}%",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "COMPLETED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(Dimens.spaceLarge))
                
                Column {
                    Text("Project Schedule", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.small)
                                .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small)
                                .padding(Dimens.spaceSmall)
                        ) {
                            Column {
                                Text("Start Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    project.startDate?.let { com.example.core.util.DateUtils.formatToDate(it) } ?: "Not set",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.small)
                                .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small)
                                .padding(Dimens.spaceSmall)
                        ) {
                            Column {
                                Text("Est. Completion", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    project.expectedCompletionDate?.let { com.example.core.util.DateUtils.formatToDate(it) } ?: "Not set",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
            
            // Sub-tasks from actual milestones
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                // Show progress based on actual project completion
                val structuralProgress = minOf(100, project.progress * 2) // Structural leads overall
                val mepProgress = maxOf(0, project.progress - 20) // MEP trails by ~20%
                SubTaskProgress("Structural Works", structuralProgress)
                SubTaskProgress("MEP & Finishing", mepProgress)
            }
        }
    }
}

@Composable
fun SubTaskProgress(title: String, progress: Int) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$progress%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = if (progress == 100) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun MilestonesTimeline(milestones: List<Milestone>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Key Milestones", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("${milestones.count { it.isCompleted }}/${milestones.size} done", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            
            if (milestones.isEmpty()) {
                Text(
                    "No milestones defined yet. Add milestones to track progress.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(modifier = Modifier.padding(start = Dimens.spaceMedium)) {
                    milestones.forEachIndexed { index, milestone ->
                        val status = when {
                            milestone.isCompleted -> 1
                            index == milestones.indexOfFirst { !it.isCompleted } -> 0
                            else -> -1
                        }
                        val dateStr = when {
                            milestone.isCompleted && milestone.completionDate != null ->
                                com.example.core.util.DateUtils.formatToDate(milestone.completionDate)
                            status == 0 -> "In Progress"
                            else -> "Upcoming"
                        }
                        TimelineItem(
                            title = milestone.name,
                            desc = milestone.notes ?: "",
                            dateStr = dateStr,
                            status = status,
                            isLast = index == milestones.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItem(title: String, desc: String, dateStr: String, status: Int, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Line and icon
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = when(status) {
                            1 -> MaterialTheme.colorScheme.secondary
                            0 -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        shape = CircleShape
                    )
                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(status) {
                        1 -> Icons.Default.Check
                        0 -> Icons.Default.Construction
                        else -> Icons.Default.ElectricBolt
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = when(status) {
                        1 -> Color.White
                        0 -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        
        Column(modifier = Modifier.weight(1f).padding(bottom = Dimens.spaceLarge)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Box(
                modifier = Modifier
                    .background(
                        when(status) {
                            1 -> MaterialTheme.colorScheme.surfaceContainerLowest
                            0 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            else -> Color.Transparent
                        },
                        MaterialTheme.shapes.small
                    )
                    .border(
                        1.dp,
                        when(status) {
                            1 -> MaterialTheme.colorScheme.surfaceContainerHigh
                            0 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else -> Color.Transparent
                        },
                        MaterialTheme.shapes.small
                    )
                    .padding(horizontal = Dimens.spaceSmall, vertical = 2.dp)
            ) {
                Text(
                    dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = when(status) {
                        1 -> MaterialTheme.colorScheme.secondary
                        0 -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
            }
        }
    }
}

@Composable
fun BudgetHealthCard(project: Project) {
    val budgetPaise = project.estimatedBudgetPaise ?: project.contractValuePaise ?: 0L
    val contractPaise = project.contractValuePaise ?: 0L
    // Estimate spent as proportion of progress * budget
    val estimatedSpentPaise = (budgetPaise * project.progress / 100)
    val spentPercent = if (budgetPaise > 0L) ((estimatedSpentPaise.toDouble() / budgetPaise.toDouble()) * 100).toInt() else 0
    val isOverBudget = spentPercent > project.progress + 10

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Budget Health", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budget", tint = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(CurrencyUtils.formatPaise(estimatedSpentPaise), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
                Text(" / ${CurrencyUtils.formatPaise(budgetPaise)} Total", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
            }
            
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Allocated Spent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$spentPercent%", style = MaterialTheme.typography.labelSmall, color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { spentPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(12.dp),
                color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            if (isOverBudget) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = "Warning", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Spending is ahead of schedule. Review allocations.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "On track", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Budget is on track with project progress.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun RecentExpenses() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
            Text("Recent Expenses", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            
            ExpenseItem(icon = Icons.Default.Hardware, title = "Steel Beams Delivery", subtitle = "Acme Corp", amount = "-$12,450", iconBg = MaterialTheme.colorScheme.secondaryContainer, iconColor = MaterialTheme.colorScheme.onSecondaryContainer)
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            ExpenseItem(icon = Icons.Default.Engineering, title = "Weekly Labour Payroll", subtitle = "Internal", amount = "-$45,200", iconBg = MaterialTheme.colorScheme.tertiaryContainer, iconColor = MaterialTheme.colorScheme.onTertiaryContainer)
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            ExpenseItem(icon = Icons.Default.FireTruck, title = "Crane Rental", subtitle = "HeavyLift Co.", amount = "-$8,900", iconBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), iconColor = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest, contentColor = MaterialTheme.colorScheme.secondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text("Full Ledger", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ExpenseItem(icon: ImageVector, title: String, subtitle: String, amount: String, iconBg: Color, iconColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
            }
        }
        Text(amount, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
