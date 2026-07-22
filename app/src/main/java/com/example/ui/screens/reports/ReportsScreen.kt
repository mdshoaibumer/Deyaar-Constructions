package com.example.ui.screens.reports

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.core.util.PdfReportGenerator
import com.example.ui.theme.Dimens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showProjectPicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    // Date Pickers
    if (showStartDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = uiState.filter.startDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { viewModel.onEvent(ReportsEvent.SelectStartDate(it)) }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    if (showEndDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = uiState.filter.endDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { viewModel.onEvent(ReportsEvent.SelectEndDate(it)) }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    // Project Selection Dialog
    if (showProjectPicker) {
        AlertDialog(
            onDismissRequest = { showProjectPicker = false },
            title = { Text("Select Project") },
            text = {
                LazyColumn {
                    item {
                        ListItem(
                            headlineContent = { Text("All Projects") },
                            modifier = Modifier.clickable {
                                viewModel.onEvent(ReportsEvent.SelectProject(null, null))
                                showProjectPicker = false
                            }
                        )
                    }
                    items(uiState.projects) { project ->
                        ListItem(
                            headlineContent = { Text(project.name) },
                            supportingContent = { Text(project.status.displayName) },
                            modifier = Modifier.clickable {
                                viewModel.onEvent(ReportsEvent.SelectProject(project.id, project.name))
                                showProjectPicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProjectPicker = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Dimens.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            // Report Type Selection
            item {
                Text("Report Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReportType.entries.forEachIndexed { index, type ->
                        SegmentedButton(
                            selected = uiState.selectedReportType == type,
                            onClick = { viewModel.onEvent(ReportsEvent.SelectReportType(type)) },
                            shape = SegmentedButtonDefaults.itemShape(index, ReportType.entries.size),
                            label = { Text(type.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
                        )
                    }
                }
            }

            // Filters
            item {
                Text("Filters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                // Project filter
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().clickable { showProjectPicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.spaceMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Project", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = uiState.filter.projectName ?: "All Projects",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                // Date range
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                    OutlinedCard(
                        modifier = Modifier.weight(1f).clickable { showStartDatePicker = true }
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceSmall)) {
                            Text("From", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = uiState.filter.startDate?.let { dateFormatter.format(Date(it)) } ?: "Start",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    OutlinedCard(
                        modifier = Modifier.weight(1f).clickable { showEndDatePicker = true }
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceSmall)) {
                            Text("To", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = uiState.filter.endDate?.let { dateFormatter.format(Date(it)) } ?: "End",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Generate Button
            item {
                Button(
                    onClick = { viewModel.onEvent(ReportsEvent.GenerateReport) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isGenerating
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                    }
                    Icon(Icons.Default.Assessment, contentDescription = null)
                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                    Text("Generate Report")
                }
            }

            // Error
            if (uiState.error != null) {
                item {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            // Report Preview
            if (uiState.reportData != null) {
                val report = uiState.reportData!!

                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(report.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row {
                            // Export to PDF
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    try {
                                        val file = withContext(Dispatchers.IO) {
                                            PdfReportGenerator.generatePdf(context, report)
                                        }
                                        viewModel.setPdfPath(file.absolutePath)
                                        Toast.makeText(context, "PDF saved: ${file.name}", Toast.LENGTH_LONG).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "PDF export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = MaterialTheme.colorScheme.error)
                            }
                            // Share
                            if (uiState.pdfPath != null) {
                                IconButton(onClick = {
                                    try {
                                        val file = java.io.File(uiState.pdfPath!!)
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Share failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share PDF")
                                }
                            }
                        }
                    }
                }

                // Summary Cards
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                            report.summaryLines.forEach { line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Report Rows
                items(report.rows) { row ->
                    ReportRowItem(row = row)
                }

                // Total
                item {
                    HorizontalDivider(thickness = 2.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = CurrencyUtils.formatPaise(report.totalAmountPaise),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (report.totalAmountPaise >= 0) com.example.ui.theme.DeyaarTheme.colors.success else MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
fun ReportRowItem(row: ReportRow) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = row.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (row.sublabel != null) {
                    Text(text = row.sublabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (row.quantity != null) {
                    Text(text = row.quantity, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (row.amountPaise != 0L) {
                Text(
                    text = CurrencyUtils.formatPaise(row.amountPaise),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (row.amountPaise >= 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
