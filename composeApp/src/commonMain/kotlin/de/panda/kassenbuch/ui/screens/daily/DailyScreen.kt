package de.panda.kassenbuch.ui.screens.daily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import de.panda.kassenbuch.ui.theme.*
import de.panda.kassenbuch.util.Formatter
import de.panda.kassenbuch.util.epochMillisToLocalDate
import de.panda.kassenbuch.util.toEpochMillis
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyContent() {
    val screenModel = koinInject<DailyScreenModel>()
    val state by screenModel.state.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.datum.toEpochMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        screenModel.loadTag(epochMillisToLocalDate(millis))
                    }
                    showDatePicker = false
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(Res.string.cancel)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.daily_title)) },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = stringResource(Res.string.select_date))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Date header
            item {
                Text(
                    Formatter.datum(state.datum),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Summary card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryRow(stringResource(Res.string.daily_vortrag), state.vortrag, SaldoBlue)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow(stringResource(Res.string.daily_einnahmen), state.einnahmen, Einnahme)
                        SummaryRow(stringResource(Res.string.daily_ausgaben), state.ausgaben, Ausgabe)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow(stringResource(Res.string.daily_endbestand), state.endbestand, SaldoBlue)
                    }
                }
            }

            // Kassensturz
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(Res.string.daily_kassensturz), style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.gezaehlterBestand,
                            onValueChange = { screenModel.updateGezaehlterBestand(it) },
                            label = { Text(stringResource(Res.string.daily_counted)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Filled.Paid, null) }
                        )
                        if (state.differenz != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${stringResource(Res.string.daily_difference)}: ${Formatter.euro(state.differenz!!)}",
                                color = if (abs(state.differenz!!) < 0.01) Einnahme else Ausgabe,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Close day / already closed
            item {
                if (state.istAbgeschlossen) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Einnahme)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(Res.string.daily_already_closed))
                        }
                    }
                } else {
                    Button(
                        onClick = { screenModel.tagesabschlussDurchfuehren() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Summarize, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.daily_close))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, betrag: Double, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(Formatter.euro(betrag), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

// ═══ Münzzähler ═══

data class CoinRow(val label: String, val value: Double, var count: Int = 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinCounterContent(onNavigateBack: () -> Unit) {
    val coins = remember {
        mutableStateListOf(
            CoinRow("500\u20ac", 500.0), CoinRow("200\u20ac", 200.0), CoinRow("100\u20ac", 100.0),
            CoinRow("50\u20ac", 50.0), CoinRow("20\u20ac", 20.0), CoinRow("10\u20ac", 10.0), CoinRow("5\u20ac", 5.0),
            CoinRow("2\u20ac", 2.0), CoinRow("1\u20ac", 1.0), CoinRow("0,50\u20ac", 0.5), CoinRow("0,20\u20ac", 0.2),
            CoinRow("0,10\u20ac", 0.1), CoinRow("0,05\u20ac", 0.05), CoinRow("0,02\u20ac", 0.02), CoinRow("0,01\u20ac", 0.01)
        )
    }
    val total = coins.sumOf { it.value * it.count }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.coin_counter_title)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${stringResource(Res.string.coin_total)}:", style = MaterialTheme.typography.headlineSmall)
                        Text(Formatter.euro(total), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = SaldoBlue)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onNavigateBack() }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(Res.string.coin_confirm))
                    }
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(coins.size) { idx ->
                val coin = coins[idx]
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(coin.label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (coin.count > 0) coins[idx] = coin.copy(count = coin.count - 1) }) {
                                Icon(Icons.Filled.Remove, "-")
                            }
                            Text("${coin.count}", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { coins[idx] = coin.copy(count = coin.count + 1) }) {
                                Icon(Icons.Filled.Add, "+")
                            }
                        }
                        Text(Formatter.euro(coin.value * coin.count), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
