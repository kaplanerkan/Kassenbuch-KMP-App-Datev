package de.panda.kassenbuch.ui.screens.database

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.panda.kassenbuch.ui.theme.Einnahme
import de.panda.kassenbuch.ui.theme.SaldoBlue
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseContent() {
    val screenModel = koinInject<DatabaseScreenModel>()
    val state by screenModel.state.collectAsState()

    var showRestoreDialog by remember { mutableStateOf<String?>(null) }

    // Restore confirmation dialog
    showRestoreDialog?.let { zipPath ->
        AlertDialog(
            onDismissRequest = { showRestoreDialog = null },
            icon = {
                Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error)
            },
            title = { Text(stringResource(Res.string.database_confirm_restore)) },
            text = { Text(stringResource(Res.string.database_restore_warning)) },
            confirmButton = {
                Button(
                    onClick = {
                        screenModel.restoreBackup(zipPath)
                        showRestoreDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.database_restore))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = null }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.database_title)) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Backup card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Backup, null, tint = SaldoBlue)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(Res.string.database_backup),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(Res.string.database_backup_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { screenModel.createBackup() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isBackingUp
                        ) {
                            if (state.isBackingUp) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                            } else {
                                Icon(Icons.Filled.Save, null)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(stringResource(Res.string.database_backup))
                        }
                    }
                }
            }

            // Backup list header
            item {
                Text(
                    stringResource(Res.string.database_backup_list),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.backupFiles.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.FolderOff, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(Res.string.database_no_backups),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.backupFiles) { zipPath ->
                    val fileName = zipPath.substringAfterLast('/').substringAfterLast('\\')
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.FolderZip, null, tint = SaldoBlue)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    fileName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            FilledTonalButton(
                                onClick = { showRestoreDialog = zipPath }
                            ) {
                                Icon(Icons.Filled.Restore, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(Res.string.database_restore), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Info card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(Res.string.database_info_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
