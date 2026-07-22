package com.example.ui.screens.resource

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.ui.theme.Dimens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.Worker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerListScreen(
    viewModel: WorkerListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Labour / Workers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Worker")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.ShimmerCardList(
                modifier = Modifier.padding(paddingValues),
                itemCount = 4
            )
        } else if (uiState.workers.isEmpty()) {
            com.example.ui.components.layout.EmptyState(
                icon = Icons.Default.Group,
                title = "No workers yet",
                description = "Add workers to manage attendance, wages, and assignments.",
                actionLabel = "Add Worker",
                onAction = onNavigateToAdd,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                items(uiState.workers, key = { it.id }) { worker ->
                    WorkerItem(worker = worker, onClick = { onNavigateToEdit(worker.id) })
                }
            }
        }
    }
}

@Composable
fun WorkerItem(worker: Worker, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(text = worker.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Dimens.spaceMicro))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Trade: ${worker.trade}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Wage: ${CurrencyUtils.formatPaise(worker.dailyWagePaise)}/day", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
