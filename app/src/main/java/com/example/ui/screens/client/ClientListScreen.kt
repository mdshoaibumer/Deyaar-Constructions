package com.example.ui.screens.client

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.components.PremiumClientCard
import com.example.ui.components.layout.EmptyState
import com.example.ui.components.layout.ShimmerCardList

@Composable
fun ClientListScreen(
    viewModel: ClientViewModel,
    onNavigateToAddClient: () -> Unit,
    onNavigateToClientDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("All Clients") }

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                onNavigationClick = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToAddClient) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add new client",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddClient,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.testTag("add_client_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Client")
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
                    text = "Clients",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search clients, projects...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                ) {
                    listOf("All Clients", "Active", "VIP", "Inactive").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest)
                                .then(
                                    if (!isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50)) else Modifier
                                )
                                .clickable {
                                    selectedFilter = filter
                                    // Reset favorites
                                    if (uiState.favoritesOnly && filter != "VIP") viewModel.toggleFavoriteFilter()
                                    when (filter) {
                                        "All Clients" -> viewModel.onStatusFilterChanged(com.example.domain.usecase.client.ClientStatusFilter.ALL)
                                        "Active" -> viewModel.onStatusFilterChanged(com.example.domain.usecase.client.ClientStatusFilter.ACTIVE)
                                        "Inactive" -> viewModel.onStatusFilterChanged(com.example.domain.usecase.client.ClientStatusFilter.INACTIVE)
                                        "VIP" -> {
                                            viewModel.onStatusFilterChanged(com.example.domain.usecase.client.ClientStatusFilter.ALL)
                                            if (!uiState.favoritesOnly) viewModel.toggleFavoriteFilter()
                                        }
                                    }
                                }
                                .padding(horizontal = Dimens.spaceMedium, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                ShimmerCardList(itemCount = 5)
            } else if (uiState.clients.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.People,
                    title = "No clients yet",
                    description = if (uiState.searchQuery.isNotEmpty()) "No clients match your search."
                    else "Add your first client to start managing contacts and projects.",
                    actionLabel = if (uiState.searchQuery.isEmpty()) "Add Client" else null,
                    onAction = if (uiState.searchQuery.isEmpty()) onNavigateToAddClient else null
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = Dimens.marginMobile,
                        end = Dimens.marginMobile,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
                ) {
                    items(uiState.clients, key = { it.id }) { client ->
                        PremiumClientCard(
                            name = client.name,
                            location = client.city ?: "N/A",
                            imageUrl = client.photoPath,
                            status = if (client.isFavorite) "VIP" else if (client.isActive) "Active" else "Inactive",
                            isVip = client.isFavorite,
                            activeProjects = "N/A", // Placeholder
                            totalValue = "N/A", // Placeholder
                            onClick = { onNavigateToClientDetails(client.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
