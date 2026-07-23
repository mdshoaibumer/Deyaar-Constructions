package com.example.ui.screens.reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.CurrencyUtils
import com.example.core.util.PdfReportGenerator
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showProjectPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                onNavigationClick = onNavigateBack,
                actions = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
        ) {
            // Header
            item {
                Column {
                    Text(
                        "Analytics & Reports",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Generate reports from your real project data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Report Type Selector
            item {
                ReportTypeSelector(
                    selectedType = uiState.selectedReportType,
                    onTypeSelected = { viewModel.onEvent(ReportsEvent.SelectReportType(it)) }
                )
            }

            // Filters
            item {
                FiltersCard(
                    filter = uiState.filter,
                    projects = uiState.projects,
                    onSelectProject = { id, name ->
                        viewModel.onEvent(ReportsEvent.SelectProject(id, name))
                    },
                    showProjectPicker = showProjectPicker,
                    onToggleProjectPicker = { showProjectPicker = it }
                )
            }

            // Generate Button
            item {
                Button(
                    onClick = { viewModel.onEvent(ReportsEvent.GenerateReport) },
                    modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
                    enabled = !uiState.isGenerating,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text("Generating...")
                    } else {
                        Icon(Icons.Default.Analytics, contentDescription = null)
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text("Generate Report")
                    }
                }
            }

            // Error
            if (uiState.error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimens.spaceMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text(
                                uiState.error!!,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Report Results
            if (uiState.reportData != null) {
                val reportData = uiState.reportData!!

                // PDF Export Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            MaterialTheme.shapes.small
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                                Column {
                                    Text(
                                        reportData.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        "Export as PDF document",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                            Button(
                                onClick = {
                                    try {
                                        val file = PdfReportGenerator.generatePdf(context, reportData)
                                        viewModel.setPdfPath(file.absolutePath)
                                        Toast.makeText(
                                            context,
                                            "PDF saved: ${file.name}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "PDF export failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download PDF")
                            }
                        }
                    }
                }

                // Summary Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                            Text(
                                "Summary",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            reportData.summaryLines.forEach { line ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                    Text(
                                        line,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Report Rows
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(Dimens.spaceMedium),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Details (${reportData.rows.size} items)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (reportData.totalAmountPaise != 0L) {
                                    Text(
                                        "Total: ${CurrencyUtils.formatPaise(reportData.totalAmountPaise)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (reportData.totalAmountPaise >= 0)
                                            Color(0xFF059669) else MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                            reportData.rows.forEachIndexed { index, row ->
                                ReportRowItem(row = row)
                                if (index < reportData.rows.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun ReportTypeSelector(
    selectedType: ReportType,
    onTypeSelected: (ReportType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                "Report Type",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            ReportType.entries.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onTypeSelected(type) }
                        .background(
                            if (type == selectedType) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = type == selectedType,
                        onClick = { onTypeSelected(type) }
                    )
                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                    Text(
                        type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (type == selectedType) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltersCard(
    filter: ReportFilter,
    projects: List<com.example.domain.model.Project>,
    onSelectProject: (String?, String?) -> Unit,
    showProjectPicker: Boolean,
    onToggleProjectPicker: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
            Text(
                "Filters",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            // Project filter
            OutlinedButton(
                onClick = { onToggleProjectPicker(!showProjectPicker) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Text(filter.projectName ?: "All Projects")
            }

            if (showProjectPicker) {
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        // "All Projects" option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectProject(null, null)
                                    onToggleProjectPicker(false)
                                }
                                .padding(Dimens.spaceMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.SelectAll,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text("All Projects", style = MaterialTheme.typography.bodyMedium)
                        }
                        HorizontalDivider()
                        projects.take(10).forEach { project ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectProject(project.id, project.name)
                                        onToggleProjectPicker(false)
                                    }
                                    .padding(Dimens.spaceMedium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    project.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    project.status.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportRowItem(row: ReportRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceMedium)
            .semantics(mergeDescendants = true) {
                contentDescription = buildString {
                    append(row.label)
                    row.sublabel?.let { append(", $it") }
                    if (row.amountPaise != 0L) append(", ${CurrencyUtils.formatPaise(row.amountPaise)}")
                    row.quantity?.let { append(", $it") }
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                row.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            row.sublabel?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            row.quantity?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        if (row.amountPaise != 0L) {
            Text(
                CurrencyUtils.formatPaise(kotlin.math.abs(row.amountPaise)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (row.amountPaise >= 0) Color(0xFF059669) else MaterialTheme.colorScheme.error
            )
        }
    }
}
