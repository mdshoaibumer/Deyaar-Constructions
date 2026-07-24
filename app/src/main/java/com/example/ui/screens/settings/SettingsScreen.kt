package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.security.BackupManager
import com.example.core.security.SecurityPreferences
import com.example.ui.components.DeyaarTopAppBar
import com.example.ui.theme.Dimens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPinSetup: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val securityPrefs = remember { SecurityPreferences(context) }
    val themePreferences = remember { com.example.core.preferences.ThemePreferences(context) }
    var isPinEnabled by remember { mutableStateOf(securityPrefs.isPinEnabled()) }
    var isBiometricEnabled by remember { mutableStateOf(securityPrefs.isBiometricEnabled()) }
    val isDarkThemeEnabled by themePreferences.isDarkModeEnabled.collectAsState(initial = false)
    var isOfflineModeEnabled by remember { mutableStateOf(false) } // Placeholder for actual offline logic
    var showBackupDialog by remember { mutableStateOf(false) }
    var backupPassword by remember { mutableStateOf("") }

    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Create Backup") },
            text = {
                Column {
                    Text("Enter a password to encrypt the backup:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = backupPassword,
                        onValueChange = { backupPassword = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (backupPassword.length >= 8) {
                        coroutineScope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    val backupManager = BackupManager(context)
                                    val file = java.io.File(context.getExternalFilesDir(null), "backup_${System.currentTimeMillis()}.dcb")
                                    java.io.FileOutputStream(file).use { out ->
                                        backupManager.exportBackup(out, backupPassword)
                                    }
                                }
                                Toast.makeText(context, "Backup created successfully!", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showBackupDialog = false
                        backupPassword = ""
                    } else {
                        Toast.makeText(context, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Backup") }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false; backupPassword = "" }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            DeyaarTopAppBar(
                title = "DEYAAR CONSTRUCTIONS",
                showLogo = false,
                onNavigationClick = onNavigateBack,
                actions = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.marginMobile),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            HeaderSection()
            
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.spaceLarge).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape).background(MaterialTheme.colorScheme.surfaceContainer)) {
                            // Avatar placeholder
                            Icon(Icons.Default.Person, contentDescription = "User avatar", modifier = Modifier.fillMaxSize().padding(8.dp), tint = MaterialTheme.colorScheme.secondary)
                        }
                        Column {
                            Text("Alex Carter", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lead Engineer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.small).border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), MaterialTheme.shapes.small).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Text("DEYAAR CONSTRUCTIONS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                Box(modifier = Modifier.padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceMedium).fillMaxWidth()) {
                    OutlinedButton(onClick = {
                        Toast.makeText(context, "Profile editing coming in next update", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit Profile", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            
            // Security Section
            SettingsSectionCard(title = "Security", icon = Icons.Default.Security) {
                SettingsActionRow(
                    icon = Icons.Default.Pin,
                    title = "Change Access PIN",
                    subtitle = "Last updated 30 days ago",
                    onClick = onNavigateToPinSetup
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                SettingsToggleRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Unlock",
                    subtitle = "Face/Touch ID required",
                    checked = isBiometricEnabled,
                    onCheckedChange = { 
                        securityPrefs.setBiometricEnabled(it)
                        isBiometricEnabled = it 
                    }
                )
            }
            
            // Local Database Section
            SettingsSectionCard(title = "Local Database", icon = Icons.Default.Storage) {
                SettingsActionRow(
                    icon = Icons.Default.CloudUpload,
                    title = "Create Backup",
                    subtitle = "Encrypt and export your data",
                    trailingContent = {
                        Button(
                            onClick = { showBackupDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Backup", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    onClick = { showBackupDialog = true }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                SettingsActionRow(
                    icon = Icons.Default.Download,
                    title = "Export CSV",
                    subtitle = "Download local cache",
                    onClick = { Toast.makeText(context, "Exporting to CSV...", Toast.LENGTH_SHORT).show() }
                )
            }
            
            // Preferences Section
            SettingsSectionCard(title = "Preferences", icon = Icons.Default.Tune) {
                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    subtitle = "High-contrast mode for site work",
                    checked = isDarkThemeEnabled,
                    onCheckedChange = { enabled ->
                        coroutineScope.launch { themePreferences.setDarkModeEnabled(enabled) }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                SettingsToggleRow(
                    icon = Icons.Default.WifiOff,
                    title = "Offline Mode",
                    subtitle = "Force local cache for blueprints",
                    checked = isOfflineModeEnabled,
                    onCheckedChange = { isOfflineModeEnabled = it }
                )
            }
            
            // Danger Zone
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceMedium), contentAlignment = Alignment.Center) {
                TextButton(onClick = {
                    securityPrefs.isAppLockEnabled = true
                    Toast.makeText(context, "Signed out. App will require unlock on next launch.", Toast.LENGTH_LONG).show()
                    // Close the activity to force re-authentication
                    (context as? android.app.Activity)?.finishAffinity()
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign out", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of Device", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column(modifier = Modifier.padding(bottom = Dimens.spaceMedium)) {
        Text("System Settings", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text("Manage configurations, security, and preferences for your local environment.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.5f)).padding(Dimens.spaceMedium).border(androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            content()
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(Dimens.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer)
        )
    }
}
