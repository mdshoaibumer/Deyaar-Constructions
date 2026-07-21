package com.example.ui.screens.sitediary

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.SiteDiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDiaryListScreen(
    viewModel: SiteDiaryListViewModel,
    onNavigateToDiaryDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.project?.name ?: "Site Diaries") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.createTodayDiary { newDiaryId ->
                        onNavigateToDiaryDetails(newDiaryId)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Today's Diary")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.diaries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No site diaries recorded yet", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.diaries, key = { it.id }) { diary ->
                    SiteDiaryCard(diary = diary, onClick = { onNavigateToDiaryDetails(diary.id) })
                }
            }
        }
    }
}

@Composable
fun SiteDiaryCard(diary: SiteDiary, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                text = dateFormat.format(Date(diary.date)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress: ${diary.overallProgress}%", style = MaterialTheme.typography.bodyMedium)
                Text(diary.weather ?: "Weather Not Logged", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!diary.workSummary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Text(
                    text = diary.workSummary!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}
