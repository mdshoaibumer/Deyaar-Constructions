package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.security.BackupManager
import com.example.core.security.SecurityPreferences
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
    var isPinEnabled by remember { mutableStateOf(securityPrefs.isPinEnabled()) }
    var isBiometricEnabled by remember { mutableStateOf(securityPrefs.isBiometricEnabled()) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
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
                    if (backupPassword.length >= 4) {
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
                        Toast.makeText(context, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
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
            TopAppBar(
                title = { Text("Settings") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Security Section
            SettingsSectionHeader("Security")

            SettingsItem(
                icon = Icons.Default.Pin,
                title = "PIN Lock",
                subtitle = if (isPinEnabled) "Enabled" else "Disabled",
                onClick = { onNavigateToPinSetup() }
            )

            SettingsToggleItem(
                icon = Icons.Default.Fingerprint,
                title = "Biometric Unlock",
                subtitle = "Use fingerprint or face to unlock",
                checked = isBiometricEnabled,
                onCheckedChange = { enabled ->
                    securityPrefs.setBiometricEnabled(enabled)
                    isBiometricEnabled = enabled
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.spaceMedium))

            // Backup Section
            SettingsSectionHeader("Data")

            SettingsItem(
                icon = Icons.Default.Backup,
                title = "Backup Database",
                subtitle = "Create encrypted backup of all data",
                onClick = { showBackupDialog = true }
            )

            SettingsItem(
                icon = Icons.Default.Restore,
                title = "Restore Database",
                subtitle = "Restore from encrypted backup file",
                onClick = {
                    Toast.makeText(context, "Use file manager to select a .dcb backup file", Toast.LENGTH_LONG).show()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.spaceMedium))

            // App Info Section
            SettingsSectionHeader("About")

            SettingsItem(
                icon = Icons.Default.Info,
                title = "App Version",
                subtitle = "1.0.0 (Build 1)",
                onClick = {}
            )

            SettingsItem(
                icon = Icons.Default.Storage,
                title = "Database",
                subtitle = "deyaar_construction.db (v9)",
                onClick = {}
            )

            SettingsItem(
                icon = Icons.Default.Business,
                title = "Company",
                subtitle = "Deyaar Constructions",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceMedium)
            .semantics { heading() }
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}
