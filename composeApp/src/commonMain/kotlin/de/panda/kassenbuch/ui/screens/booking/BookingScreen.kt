package de.panda.kassenbuch.ui.screens.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import de.panda.kassenbuch.data.entity.BuchungsArt
import de.panda.kassenbuch.platform.ReceiptPhotoCapture
import de.panda.kassenbuch.ui.theme.Ausgabe
import de.panda.kassenbuch.ui.theme.Einnahme
import de.panda.kassenbuch.util.Formatter
import de.panda.kassenbuch.util.epochMillisToLocalDate
import de.panda.kassenbuch.util.toEpochMillis
import kassenbuch.composeapp.generated.resources.Res
import kassenbuch.composeapp.generated.resources.back
import kassenbuch.composeapp.generated.resources.booking_amount
import kassenbuch.composeapp.generated.resources.booking_beleg
import kassenbuch.composeapp.generated.resources.booking_bu_key
import kassenbuch.composeapp.generated.resources.booking_date
import kassenbuch.composeapp.generated.resources.booking_description
import kassenbuch.composeapp.generated.resources.booking_edit
import kassenbuch.composeapp.generated.resources.booking_error_amount
import kassenbuch.composeapp.generated.resources.booking_error_text
import kassenbuch.composeapp.generated.resources.booking_gegenkonto
import kassenbuch.composeapp.generated.resources.booking_new
import kassenbuch.composeapp.generated.resources.booking_no_bookings_day
import kassenbuch.composeapp.generated.resources.booking_note
import kassenbuch.composeapp.generated.resources.booking_save
import kassenbuch.composeapp.generated.resources.booking_tax_rate
import kassenbuch.composeapp.generated.resources.booking_time
import kassenbuch.composeapp.generated.resources.booking_type
import kassenbuch.composeapp.generated.resources.bu_prefix
import kassenbuch.composeapp.generated.resources.cancel
import kassenbuch.composeapp.generated.resources.error_prefix
import kassenbuch.composeapp.generated.resources.nav_bookings
import kassenbuch.composeapp.generated.resources.ok
import kassenbuch.composeapp.generated.resources.select_date
import kassenbuch.composeapp.generated.resources.select_time
import kassenbuch.composeapp.generated.resources.type_ausgabe
import kassenbuch.composeapp.generated.resources.type_bankeinzahlung
import kassenbuch.composeapp.generated.resources.type_buerokosten
import kassenbuch.composeapp.generated.resources.type_einnahme
import kassenbuch.composeapp.generated.resources.type_gutschein_verkauf
import kassenbuch.composeapp.generated.resources.type_privateinlage
import kassenbuch.composeapp.generated.resources.type_privatentnahme
import kassenbuch.composeapp.generated.resources.type_sonstige_ausgabe
import kassenbuch.composeapp.generated.resources.type_sonstige_einnahme
import kassenbuch.composeapp.generated.resources.type_tagesumsatz_19
import kassenbuch.composeapp.generated.resources.type_tagesumsatz_7
import kassenbuch.composeapp.generated.resources.type_wareneinkauf_19
import kassenbuch.composeapp.generated.resources.type_wareneinkauf_7
import kassenbuch.composeapp.generated.resources.type_wechselgeld_aus
import kassenbuch.composeapp.generated.resources.type_wechselgeld_ein
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// ═══════════════════════════════════════
// BOOKING LIST (Tab Content)
// ═══════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListContent(
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToNew: () -> Unit
) {
    val screenModel = koinInject<BookingScreenModel>()
    val listState by screenModel.listState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = listState.filterDatum.toEpochMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        screenModel.loadBuchungenForDate(epochMillisToLocalDate(millis))
                    }
                    showDatePicker = false
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${stringResource(Res.string.nav_bookings)} \u2014 ${Formatter.datum(listState.filterDatum)}"
                    )
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = stringResource(Res.string.select_date)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNew) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(Res.string.booking_new))
            }
        }
    ) { padding ->
        if (listState.buchungen.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(Res.string.booking_no_bookings_day))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(listState.buchungen, key = { it.id }) { buchung ->
                    Card(
                        onClick = { onNavigateToEdit(buchung.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (buchung.istEinnahme) Icons.Filled.ArrowDownward
                                else Icons.Filled.ArrowUpward,
                                contentDescription = null,
                                tint = if (buchung.istEinnahme) Einnahme else Ausgabe
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    buchung.buchungstext,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${Formatter.uhrzeit(buchung.uhrzeit)} \u00b7 ${buchung.belegNr} \u00b7 ${Formatter.kontoLabel(buchung.gegenkonto)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${if (buchung.istEinnahme) "+" else "\u2212"}${Formatter.euro(buchung.betrag)}",
                                fontWeight = FontWeight.Bold,
                                color = if (buchung.istEinnahme) Einnahme else Ausgabe
                            )
                        }
                    }
                }

                // Spacer for FAB
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

// ═══════════════════════════════════════
// BOOKING FORM (Pushed Screen)
// ═══════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormContent(
    bookingId: Long?,
    onNavigateBack: () -> Unit
) {
        val screenModel = koinInject<BookingScreenModel>()
        val formState by screenModel.formState.collectAsState()

        LaunchedEffect(bookingId) {
            if (bookingId != null && bookingId > 0) {
                screenModel.loadBuchung(bookingId)
            } else {
                screenModel.resetForm()
            }
        }

        LaunchedEffect(formState.savedSuccess) {
            if (formState.savedSuccess) onNavigateBack()
        }

        var expanded by remember { mutableStateOf(false) }
        var showEinnahme by remember(formState.istNeu, formState.id) {
            mutableStateOf(formState.buchungsart.einnahme)
        }

        // Date Picker
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.datum.toEpochMillis()
        )

        // Time Picker
        var showTimePicker by remember { mutableStateOf(false) }
        val timePickerState = rememberTimePickerState(
            initialHour = formState.uhrzeit.hour,
            initialMinute = formState.uhrzeit.minute,
            is24Hour = true
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            screenModel.updateDatum(epochMillisToLocalDate(millis))
                        }
                        showDatePicker = false
                    }) { Text(stringResource(Res.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text(stringResource(Res.string.select_time)) },
                text = { TimePicker(state = timePickerState) },
                confirmButton = {
                    TextButton(onClick = {
                        screenModel.updateUhrzeit(
                            LocalTime(timePickerState.hour, timePickerState.minute)
                        )
                        showTimePicker = false
                    }) { Text(stringResource(Res.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(
                                if (formState.istNeu) Res.string.booking_new
                                else Res.string.booking_edit
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onNavigateBack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Datum & Uhrzeit
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = Formatter.datum(formState.datum),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(Res.string.booking_date)) },
                            leadingIcon = {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Filled.EditCalendar,
                                        contentDescription = stringResource(Res.string.select_date)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = Formatter.uhrzeit(formState.uhrzeit),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(Res.string.booking_time)) },
                            leadingIcon = {
                                Icon(Icons.Filled.Schedule, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(
                                        Icons.Filled.Schedule,
                                        contentDescription = stringResource(Res.string.select_time)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Einnahme / Ausgabe Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = showEinnahme,
                            onClick = { showEinnahme = true },
                            label = { Text(stringResource(Res.string.type_einnahme)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.ArrowDownward,
                                    contentDescription = null,
                                    tint = Einnahme
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !showEinnahme,
                            onClick = { showEinnahme = false },
                            label = { Text(stringResource(Res.string.type_ausgabe)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.ArrowUpward,
                                    contentDescription = null,
                                    tint = Ausgabe
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Buchungsart Dropdown
                item {
                    val arten = if (showEinnahme) BuchungsArt.einnahmeArten()
                    else BuchungsArt.ausgabeArten()

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = stringResource(buchungsArtStringRes(formState.buchungsart)),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(Res.string.booking_type)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            arten.forEach { art ->
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(buchungsArtStringRes(art)))
                                    },
                                    onClick = {
                                        screenModel.updateBuchungsart(art)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Betrag
                item {
                    OutlinedTextField(
                        value = formState.betrag,
                        onValueChange = { screenModel.updateBetrag(it) },
                        label = { Text(stringResource(Res.string.booking_amount)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Euro, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.errorMessage == "booking_error_amount"
                    )
                }

                // Buchungstext
                item {
                    OutlinedTextField(
                        value = formState.buchungstext,
                        onValueChange = { screenModel.updateBuchungstext(it) },
                        label = { Text(stringResource(Res.string.booking_description)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.errorMessage == "booking_error_text"
                    )
                }

                // Beleg-Nr
                item {
                    OutlinedTextField(
                        value = formState.belegNr,
                        onValueChange = { screenModel.updateBelegNr(it) },
                        label = { Text(stringResource(Res.string.booking_beleg)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Gegenkonto (readonly)
                item {
                    OutlinedTextField(
                        value = Formatter.kontoLabel(formState.gegenkonto),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.booking_gegenkonto)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Steuersatz & BU-Schluessel
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = Formatter.steuersatzLabel(formState.steuersatz),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(Res.string.booking_tax_rate)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = "${stringResource(Res.string.bu_prefix)}: ${formState.buSchluessel}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(Res.string.booking_bu_key)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Beleg-Foto (platform-specific)
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ReceiptPhotoCapture(
                                currentPhotoUri = formState.belegFotoUri,
                                onPhotoTaken = { screenModel.updateBelegFoto(it) }
                            )
                        }
                    }
                }

                // Notiz
                item {
                    OutlinedTextField(
                        value = formState.notiz,
                        onValueChange = { screenModel.updateNotiz(it) },
                        label = { Text(stringResource(Res.string.booking_note)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                // Error message
                formState.errorMessage?.let { errorKey ->
                    item {
                        val errorText = when (errorKey) {
                            "booking_error_amount" -> stringResource(Res.string.booking_error_amount)
                            "booking_error_text" -> stringResource(Res.string.booking_error_text)
                            else -> "${stringResource(Res.string.error_prefix)}: $errorKey"
                        }
                        Text(
                            errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Save Button
                item {
                    Button(
                        onClick = { screenModel.save() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !formState.isSaving
                    ) {
                        if (formState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Filled.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(Res.string.booking_save))
                        }
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
}

/**
 * Maps a [BuchungsArt] to its corresponding [StringResource] based on [BuchungsArt.labelKey].
 */
@Composable
fun buchungsArtStringRes(art: BuchungsArt): StringResource {
    return when (art.labelKey) {
        "type_tagesumsatz_7" -> Res.string.type_tagesumsatz_7
        "type_tagesumsatz_19" -> Res.string.type_tagesumsatz_19
        "type_gutschein_verkauf" -> Res.string.type_gutschein_verkauf
        "type_wechselgeld_ein" -> Res.string.type_wechselgeld_ein
        "type_privateinlage" -> Res.string.type_privateinlage
        "type_sonstige_einnahme" -> Res.string.type_sonstige_einnahme
        "type_wareneinkauf_7" -> Res.string.type_wareneinkauf_7
        "type_wareneinkauf_19" -> Res.string.type_wareneinkauf_19
        "type_wechselgeld_aus" -> Res.string.type_wechselgeld_aus
        "type_privatentnahme" -> Res.string.type_privatentnahme
        "type_bankeinzahlung" -> Res.string.type_bankeinzahlung
        "type_buerokosten" -> Res.string.type_buerokosten
        "type_sonstige_ausgabe" -> Res.string.type_sonstige_ausgabe
        else -> Res.string.type_sonstige_ausgabe
    }
}
