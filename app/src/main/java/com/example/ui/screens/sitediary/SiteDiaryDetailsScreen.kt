package com.example.ui.screens.sitediary

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDiaryDetailsScreen(
    viewModel: SiteDiaryDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val dateStr = uiState.diaryDetails?.diary?.date?.let {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Diary Details"
                    Text(dateStr) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(SiteDiaryEvent.Save) }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.diaryDetails != null) {
            val details = uiState.diaryDetails!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header / Weather
                Card(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium)) {
                    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                        Text("Weather & Conditions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        OutlinedTextField(
                            value = details.diary.weather ?: "",
                            onValueChange = { viewModel.onEvent(SiteDiaryEvent.UpdateWeather(it)) },
                            label = { Text("Weather") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        // Progress Slider
                        Text("Overall Progress: ${details.diary.overallProgress}%", style = MaterialTheme.typography.bodyMedium)
                        Slider(
                            value = details.diary.overallProgress.toFloat() / 100f,
                            onValueChange = { viewModel.onEvent(SiteDiaryEvent.UpdateOverallProgress((it * 100).toInt())) }
                        )
                    }
                }
                
                // Work Summary
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                        Text("Work Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        OutlinedTextField(
                            value = details.diary.workSummary ?: "",
                            onValueChange = { viewModel.onEvent(SiteDiaryEvent.UpdateWorkSummary(it)) },
                            label = { Text("Summary") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
                
                // Work Items
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                        Text("Work Items (${details.workItems.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        // List items here...
                        if (details.workItems.isEmpty()) {
                            Text("No items added yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            details.workItems.forEach { item ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.description, modifier = Modifier.weight(1f))
                                    Text("${item.percentageComplete}%")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        OutlinedButton(onClick = { /* Open Add Work Item Dialog */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Add Work Item")
                        }
                    }
                }

                // Placeholder for Labour/Material/Expenses
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                        Text("Resource Tracking", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            OutlinedButton(onClick = { /* Add Labour */ }) { Text("Labour") }
                            OutlinedButton(onClick = { /* Add Material */ }) { Text("Material") }
                            OutlinedButton(onClick = { /* Add Expense */ }) { Text("Expense") }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
