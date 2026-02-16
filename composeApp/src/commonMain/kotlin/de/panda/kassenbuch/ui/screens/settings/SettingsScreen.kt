package de.panda.kassenbuch.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.panda.kassenbuch.LocalThemeChanger
import de.panda.kassenbuch.util.PrefsManager
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(onNavigateBack: () -> Unit) {
    val prefs = koinInject<PrefsManager>()
    val themeChanger = LocalThemeChanger.current

    var betriebsName by remember { mutableStateOf(prefs.betriebsName) }
    var adresse by remember { mutableStateOf(prefs.adresse) }
    var steuerNummer by remember { mutableStateOf(prefs.steuerNummer) }
    var beraterNr by remember { mutableStateOf(prefs.beraterNr) }
    var mandantNr by remember { mutableStateOf(prefs.mandantNr) }
    var sachkontoLaenge by remember { mutableStateOf(prefs.sachkontoLaenge.toString()) }
    var wechselgeld by remember { mutableStateOf(prefs.standardWechselgeld.toString()) }
    var themeMode by remember { mutableStateOf(prefs.themeMode) }
    var showSnackbar by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val savedText = stringResource(Res.string.settings_saved)
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(savedText)
            showSnackbar = false
        }
    }

    val themeOptions = listOf(
        "system" to stringResource(Res.string.settings_theme_system),
        "light" to stringResource(Res.string.settings_theme_light),
        "dark" to stringResource(Res.string.settings_theme_dark)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { Text(stringResource(Res.string.settings_business), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            item {
                OutlinedTextField(value = betriebsName, onValueChange = { betriebsName = it },
                    label = { Text(stringResource(Res.string.settings_business_name)) }, modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Store, null) })
            }
            item {
                OutlinedTextField(value = adresse, onValueChange = { adresse = it },
                    label = { Text(stringResource(Res.string.settings_address)) }, modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.LocationOn, null) })
            }
            item {
                OutlinedTextField(value = steuerNummer, onValueChange = { steuerNummer = it },
                    label = { Text(stringResource(Res.string.settings_tax_number)) }, modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Numbers, null) })
            }

            item { HorizontalDivider(); Spacer(Modifier.height(4.dp)) }
            item { Text(stringResource(Res.string.settings_datev), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = beraterNr, onValueChange = { beraterNr = it },
                        label = { Text(stringResource(Res.string.settings_berater_nr)) }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = mandantNr, onValueChange = { mandantNr = it },
                        label = { Text(stringResource(Res.string.settings_mandant_nr)) }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }

            item {
                OutlinedTextField(value = sachkontoLaenge, onValueChange = { sachkontoLaenge = it },
                    label = { Text(stringResource(Res.string.settings_sachkonto_length)) }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Filled.Pin, null) })
            }

            item { HorizontalDivider(); Spacer(Modifier.height(4.dp)) }
            item { Text(stringResource(Res.string.settings_kasse), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            item {
                OutlinedTextField(value = wechselgeld, onValueChange = { wechselgeld = it },
                    label = { Text(stringResource(Res.string.settings_wechselgeld)) }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Icon(Icons.Filled.Euro, null) })
            }

            item { HorizontalDivider(); Spacer(Modifier.height(4.dp)) }
            item { Text(stringResource(Res.string.settings_design), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            item {
                ExposedDropdownMenuBox(
                    expanded = themeExpanded,
                    onExpandedChange = { themeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = themeOptions.first { it.first == themeMode }.second,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.settings_theme)) },
                        leadingIcon = { Icon(Icons.Filled.Palette, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(themeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = themeExpanded,
                        onDismissRequest = { themeExpanded = false }
                    ) {
                        themeOptions.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    themeMode = value
                                    themeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        prefs.betriebsName = betriebsName
                        prefs.adresse = adresse
                        prefs.steuerNummer = steuerNummer
                        prefs.beraterNr = beraterNr
                        prefs.mandantNr = mandantNr
                        prefs.sachkontoLaenge = sachkontoLaenge.toIntOrNull() ?: 4
                        prefs.standardWechselgeld = wechselgeld.replace(",", ".").toDoubleOrNull() ?: 100.0
                        prefs.themeMode = themeMode
                        themeChanger(themeMode)
                        showSnackbar = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.settings_save))
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(Res.string.settings_version),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
