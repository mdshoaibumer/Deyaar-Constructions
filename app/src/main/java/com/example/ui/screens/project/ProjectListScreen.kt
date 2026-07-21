package com.example.ui.screens.project

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onNavigateToAddProject: () -> Unit,
    onNavigateToProjectDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projects") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Sort Options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Newest First") }, onClick = { expanded = false })
                        DropdownMenuItem(text = { Text("Oldest First") }, onClick = { expanded = false })
                        DropdownMenuItem(text = { Text("Highest Value") }, onClick = { expanded = false })
                        DropdownMenuItem(text = { Text("Completion %") }, onClick = { expanded = false })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddProject) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spaceMedium),
                placeholder = { Text("Search projects...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Simple status filter for now
            ScrollableTabRow(
                selectedTabIndex = uiState.projects.indexOfFirst { false }.coerceAtLeast(0), // Dummy
                edgePadding = 16.dp,
                divider = {}
            ) {
                FilterChip(
                    selected = true,
                    onClick = { viewModel.onStatusFilterSelected(null) },
                    label = { Text("All") },
                    modifier = Modifier.padding(end = Dimens.spaceSmall)
                )
                ProjectStatus.entries.forEach { status ->
                    FilterChip(
                        selected = false,
                        onClick = { viewModel.onStatusFilterSelected(status) },
                        label = { Text(status.displayName) },
                        modifier = Modifier.padding(end = Dimens.spaceSmall)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No projects found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.projects, key = { it.id }) { project ->
                        ProjectCard(project = project, onClick = onNavigateToProjectDetails)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, onClick: (String) -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(java.util.Locale.Builder().setLanguage("en").setRegion("IN").build())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(project.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.projectNumber,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                AssistChip(
                    onClick = { },
                    label = { Text(project.status.displayName) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when(project.status) {
                            ProjectStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                            ProjectStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
                            ProjectStatus.PLANNING -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spaceMicro))
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = project.category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Contract Value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(project.contractValuePaise?.let { CurrencyUtils.formatPaise(it) } ?: "N/A", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Progress", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${project.progress}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}
