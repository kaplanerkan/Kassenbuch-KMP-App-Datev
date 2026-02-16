package de.panda.kassenbuch.ui.screens.export

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import de.panda.kassenbuch.platform.PlatformShareButton
import de.panda.kassenbuch.ui.theme.Einnahme
import de.panda.kassenbuch.util.Formatter
import de.panda.kassenbuch.util.epochMillisToLocalDate
import de.panda.kassenbuch.util.toEpochMillis
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportContent() {
    val screenModel = koinInject<ExportScreenModel>()
    val state by screenModel.state.collectAsState()

    var showVonPicker by remember { mutableStateOf(false) }
    var showBisPicker by remember { mutableStateOf(false) }

    val vonPickerState = rememberDatePickerState(initialSelectedDateMillis = state.von.toEpochMillis())
    val bisPickerState = rememberDatePickerState(initialSelectedDateMillis = state.bis.toEpochMillis())

    if (showVonPicker) {
        DatePickerDialog(
            onDismissRequest = { showVonPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vonPickerState.selectedDateMillis?.let { screenModel.updateVon(epochMillisToLocalDate(it)) }
                    showVonPicker = false
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showVonPicker = false }) { Text(stringResource(Res.string.cancel)) }
            }
        ) { DatePicker(state = vonPickerState) }
    }

    if (showBisPicker) {
        DatePickerDialog(
            onDismissRequest = { showBisPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    bisPickerState.selectedDateMillis?.let { screenModel.updateBis(epochMillisToLocalDate(it)) }
                    showBisPicker = false
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showBisPicker = false }) { Text(stringResource(Res.string.cancel)) }
            }
        ) { DatePicker(state = bisPickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.export_title)) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Zeitraum
            item {
                Text(stringResource(Res.string.export_period), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = Formatter.datum(state.von),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.export_from)) },
                        trailingIcon = {
                            IconButton(onClick = { showVonPicker = true }) {
                                Icon(Icons.Filled.EditCalendar, null)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = Formatter.datum(state.bis),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.export_to)) },
                        trailingIcon = {
                            IconButton(onClick = { showBisPicker = true }) {
                                Icon(Icons.Filled.EditCalendar, null)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // DATEV Export
            item {
                ExportCard(
                    icon = Icons.Filled.AccountBalance,
                    title = stringResource(Res.string.export_datev_title),
                    description = stringResource(Res.string.export_datev_desc),
                    buttonText = stringResource(Res.string.export_datev_button),
                    isLoading = state.isExporting,
                    onClick = { screenModel.exportDatev() }
                )
            }

            // Simple CSV
            item {
                ExportCard(
                    icon = Icons.Filled.TableChart,
                    title = stringResource(Res.string.export_csv_title),
                    description = stringResource(Res.string.export_csv_desc),
                    buttonText = stringResource(Res.string.export_csv_button),
                    isLoading = state.isExporting,
                    onClick = { screenModel.exportSimpleCsv() }
                )
            }

            // Success/Error
            if (state.successMessage != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Row(Modifier.fillMaxWidth().padding(16.dp)) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Einnahme)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(state.successMessage!!, fontWeight = FontWeight.Medium)
                                if (state.exportedFilePath != null) {
                                    Spacer(Modifier.height(8.dp))
                                    PlatformShareButton(filePath = state.exportedFilePath!!, modifier = Modifier)
                                }
                            }
                        }
                    }
                }
            }
            if (state.errorMessage != null) {
                item {
                    Text(
                        "${stringResource(Res.string.error_prefix)}: ${state.errorMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // DATEV Info
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(Res.string.export_datev_info_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(Res.string.export_datev_info_bu), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(Res.string.export_datev_info_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportCard(
    icon: ImageVector,
    title: String,
    description: String,
    buttonText: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Icon(Icons.Filled.FileDownload, null)
                    Spacer(Modifier.width(8.dp))
                    Text(buttonText)
                }
            }
        }
    }
}
