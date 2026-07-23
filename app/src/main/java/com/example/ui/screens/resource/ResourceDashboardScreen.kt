package com.example.ui.screens.resource

import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DeyaarTopAppBar

@Composable
fun ResourceDashboardScreen(
    viewModel: ResourceDashboardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMaterials: () -> Unit,
    onNavigateToWorkers: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToPayroll: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = true,
                onNavigationClick = onNavigateBack,
                actions = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { paddingValues ->
        if (uiState.isLoading) {
            com.example.ui.components.layout.ShimmerDashboard(modifier = Modifier.padding(paddingValues))
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Labour Overview",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "DEYAAR CONSTRUCTIONS",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onNavigateToWorkers,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Worker", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Worker")
                }
            }

            // Summary Cards
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    SummaryCard(
                        title = "Active Workers",
                        value = uiState.activeWorkers.toString(),
                        icon = Icons.Default.Engineering,
                        iconBg = Color(0xFFE0F2FE),
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${uiState.totalWorkers} total registered", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                    
                    SummaryCard(
                        title = "Present Today",
                        value = uiState.presentToday.toString(),
                        icon = Icons.Default.HowToReg,
                        iconBg = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            val attendance = if (uiState.activeWorkers > 0) uiState.presentToday.toFloat() / uiState.activeWorkers else 0f
                            LinearProgressIndicator(
                                progress = { attendance },
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${(attendance * 100).toInt()}% of active force", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.End))
                        }
                    }
                }
                
                SummaryCard(
                    title = "On Leave / Absent",
                    value = (uiState.absentToday + uiState.onLeaveToday).toString(),
                    icon = Icons.Default.Sick,
                    iconBg = MaterialTheme.colorScheme.errorContainer,
                    iconTint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.absentToday + uiState.onLeaveToday > 0) {
                        Text("Requires attention", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("All workers accounted for", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            
            // Financial Overview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                    Text("Financial Overview", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        Box(
                            modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.small)
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small).padding(Dimens.spaceMedium)
                        ) {
                            Column {
                                Text("Weekly Wages", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    com.example.core.util.CurrencyUtils.formatPaise(uiState.weeklyWagesPaise),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.small)
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small).padding(Dimens.spaceMedium)
                        ) {
                            Column {
                                Text("Monthly Payroll", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    com.example.core.util.CurrencyUtils.formatPaise(uiState.monthlyPayrollPaise),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        OutlinedButton(
                            onClick = onNavigateToAttendance,
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = "Attendance", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attendance")
                        }
                        Button(
                            onClick = onNavigateToPayroll,
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = "Payroll", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Payroll")
                        }
                    }
                }
            }

            // Recent Activities
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLowest).padding(Dimens.spaceMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Clock-ins", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("View All", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    
                    if (uiState.recentClockIns.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceLarge), contentAlignment = Alignment.Center) {
                            Text("No clock-ins recorded today", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        uiState.recentClockIns.forEachIndexed { index, item ->
                            RecentActivityItem(
                                initial = item.initial,
                                name = item.name,
                                role = item.role,
                                time = item.time,
                                status = if (item.isLate) "Late" else "On Time",
                                isLate = item.isLate
                            )
                            if (index < uiState.recentClockIns.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(modifier = Modifier.size(32.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Text(value, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            content()
        }
    }
}

@Composable
fun RecentActivityItem(initial: String, name: String, role: String, time: String, status: String, isLate: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(Dimens.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
            Box(
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initial, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column {
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(role, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
            Box(
                modifier = Modifier.background(
                    if (isLate) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else Color(0xFFF0FDF4),
                    MaterialTheme.shapes.small
                ).padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    status,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isLate) MaterialTheme.colorScheme.primary else Color(0xFF16A34A)
                )
            }
        }
    }
}
