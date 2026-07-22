package com.example.ui.screens.resource

import com.example.ui.theme.Dimens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMaterials: () -> Unit,
    onNavigateToWorkers: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToAttendance: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resource Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.spaceMedium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            ResourceCard(
                title = "Daily Attendance",
                description = "Mark daily attendance for all workers.",
                icon = Icons.Default.CalendarToday,
                onClick = onNavigateToAttendance
            )

            ResourceCard(
                title = "Materials & Inventory",
                description = "Manage cement, steel, bricks, stock levels.",
                icon = Icons.Default.Build,
                onClick = onNavigateToMaterials
            )
            
            ResourceCard(
                title = "Labour & Workforce",
                description = "Manage workers, trades, attendance, wages.",
                icon = Icons.Default.Group,
                onClick = onNavigateToWorkers
            )
            
            ResourceCard(
                title = "Suppliers & Vendors",
                description = "Manage contacts, ledgers, materials supplied.",
                icon = Icons.Default.LocalShipping,
                onClick = onNavigateToSuppliers
            )
        }
    }
}

@Composable
fun ResourceCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
