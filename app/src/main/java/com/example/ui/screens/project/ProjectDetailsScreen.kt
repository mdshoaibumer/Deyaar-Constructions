package com.example.ui.screens.project

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectTimelineEvent
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    onNavigateToDocumentation: () -> Unit = {},

    viewModel: ProjectDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProject: (String) -> Unit,
    onNavigateToSiteDiaries: (String) -> Unit = {},
    onNavigateToFinanceLedger: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Workspace") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    uiState.project?.let { project ->
                        IconButton(onClick = { onNavigateToEditProject(project.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.project != null) {
            val project = uiState.project!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Dimens.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                item {
                    ProjectOverviewHeader(project, uiState.clientName)
                }

                item {
                    FinancialSummaryCard(project)
                }

                item {
                    QuickActionsPlaceholder(
                        onNavigateToSiteDiaries = { onNavigateToSiteDiaries(project.id) },
                        onNavigateToFinanceLedger = { onNavigateToFinanceLedger(project.id) },
                        onNavigateToDocumentation = onNavigateToDocumentation
                    )
                }


                
                item {
                    ProgressSection(project)
                }

                item {
                    Text("Milestones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(uiState.milestones, key = { it.id }) { milestone ->
                    MilestoneItem(
                        milestone = milestone,
                        onToggle = { viewModel.toggleMilestone(milestone) }
                    )
                }
                
                item {
                    var newMilestoneName by remember { mutableStateOf("") }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newMilestoneName,
                            onValueChange = { newMilestoneName = it },
                            placeholder = { Text("New Milestone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        IconButton(onClick = {
                            if (newMilestoneName.isNotBlank()) {
                                viewModel.addMilestone(newMilestoneName)
                                newMilestoneName = ""
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }

                item {
                    Text("Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(uiState.timelineEvents, key = { it.id }) { event ->
                    TimelineEventItem(event)
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun ProjectOverviewHeader(project: Project, clientName: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = project.name.take(1).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        Text(
            text = project.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${project.projectNumber} • $clientName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FinancialSummaryCard(project: Project) {
    val formatter = NumberFormat.getCurrencyInstance(java.util.Locale.Builder().setLanguage("en").setRegion("IN").build())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text("Financials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Contract Value", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(project.contractValuePaise?.let { CurrencyUtils.formatPaise(it) } ?: "N/A", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(Dimens.spaceMicro))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Est. Budget", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(project.estimatedBudgetPaise?.let { CurrencyUtils.formatPaise(it) } ?: "N/A")
            }
        }
    }
}

@Composable
fun ProgressSection(project: Project) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Overall Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${project.progress}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier.fillMaxWidth().height(Dimens.spaceSmall),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                drawStopIndicator = {}
            )
        }
    }
}

@Composable
fun MilestoneItem(milestone: Milestone, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = milestone.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            Column {
                Text(
                    text = milestone.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (milestone.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (milestone.completionDate != null) {
                    val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(milestone.completionDate))
                    Text(text = "Completed: $date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun TimelineEventItem(event: ProjectTimelineEvent) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        Column {
            Text(text = event.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            val date = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(event.timestamp))
            Text(text = date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsPlaceholder(onNavigateToSiteDiaries: () -> Unit = {},
    onNavigateToFinanceLedger: () -> Unit = {},
    onNavigateToDocumentation: () -> Unit = {}) {
    Column {
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
        ) {
            AssistChip(
                onClick = onNavigateToSiteDiaries,
                label = { Text("Daily Site Diary") }
            )
            AssistChip(
                onClick = onNavigateToFinanceLedger,
                label = { Text("Financial Ledger") }
            )
            AssistChip(
                onClick = onNavigateToDocumentation,
                label = { Text("Documentation & Photos") },
                leadingIcon = { Icon(Icons.Outlined.Folder, contentDescription = null) }
            )
            AssistChip(
                onClick = onNavigateToFinanceLedger,
                label = { Text("Financial Ledger") },
                leadingIcon = { Icon(Icons.Outlined.AccountBalance, contentDescription = null) }
            )
            AssistChip(
                onClick = onNavigateToSiteDiaries,
                label = { Text("Daily Site Diaries") },
                leadingIcon = { Icon(Icons.Outlined.Book, contentDescription = null) }
            )
        }
    }
}

@Composable
fun DocumentsPlaceholder() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text("Documents & Files", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Add, contentDescription = "Upload Document", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Text("No documents uploaded yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
