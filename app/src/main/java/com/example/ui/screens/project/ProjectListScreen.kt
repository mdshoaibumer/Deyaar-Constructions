package com.example.ui.screens.project

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.core.util.CurrencyUtils
import com.example.domain.model.Project
import com.example.domain.model.ProjectStatus
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.components.PremiumProjectCard
import com.example.ui.components.layout.EmptyState
import com.example.ui.components.layout.ShimmerCardList

@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onNavigateToAddProject: () -> Unit,
    onNavigateToProjectDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedStatus by remember { mutableStateOf<ProjectStatus?>(null) }

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                onNavigationClick = onNavigateBack, // We don't really need a back button if it's top level, but keeping it
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Add, // Placeholder for notifications or add
                            contentDescription = "Add Project"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProject,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.marginMobile)
            ) {
                Text(
                    text = "Projects Overview",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Search field
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search projects, IDs, or locations...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Status filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                ) {
                    CustomFilterChip(
                        selected = selectedStatus == ProjectStatus.ACTIVE,
                        label = "Active",
                        icon = Icons.Default.CheckCircle,
                        onClick = {
                            selectedStatus = if (selectedStatus == ProjectStatus.ACTIVE) null else ProjectStatus.ACTIVE
                            viewModel.onStatusFilterSelected(selectedStatus)
                        }
                    )
                    CustomFilterChip(
                        selected = selectedStatus == ProjectStatus.ON_HOLD,
                        label = "On Hold",
                        icon = Icons.Default.PauseCircle,
                        onClick = {
                            selectedStatus = if (selectedStatus == ProjectStatus.ON_HOLD) null else ProjectStatus.ON_HOLD
                            viewModel.onStatusFilterSelected(selectedStatus)
                        }
                    )
                    CustomFilterChip(
                        selected = selectedStatus == ProjectStatus.COMPLETED,
                        label = "Completed",
                        icon = Icons.Default.TaskAlt,
                        onClick = {
                            selectedStatus = if (selectedStatus == ProjectStatus.COMPLETED) null else ProjectStatus.COMPLETED
                            viewModel.onStatusFilterSelected(selectedStatus)
                        }
                    )
                }
            }

            // Content
            when {
                uiState.isLoading -> {
                    ShimmerCardList(itemCount = 4)
                }
                uiState.projects.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.Folder,
                        title = "No projects found",
                        description = "There are no projects matching your criteria.",
                        actionLabel = "Add Project",
                        onAction = onNavigateToAddProject
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = Dimens.marginMobile,
                            end = Dimens.marginMobile,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
                    ) {
                        items(uiState.projects, key = { it.id }) { project ->
                            // Use the new PremiumProjectCard
                            PremiumProjectCard(
                                title = project.name,
                                subtitle = "Location not available", // Domain model needs location
                                imageUrl = null, // Backend doesn't have image yet
                                status = project.status.displayName,
                                timeline = "Est. 2025",
                                budget = project.contractValuePaise?.let { CurrencyUtils.formatPaise(it) } ?: "N/A",
                                progress = project.progress,
                                phase = project.category.displayName,
                                onClick = { onNavigateToProjectDetails(project.id) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomFilterChip(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(300),
        label = "chip_container_color"
    )
    
    val animatedContentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "chip_content_color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(stiffness = 400f, damping = 12f),
        label = "chip_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .background(animatedContainerColor)
            .then(
                if (!selected) Modifier.border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50)) else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = Dimens.spaceMedium, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = animatedContentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = animatedContentColor
            )
        }
    }
}
