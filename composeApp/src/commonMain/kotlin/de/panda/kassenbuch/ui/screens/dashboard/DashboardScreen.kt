package de.panda.kassenbuch.ui.screens.dashboard

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import de.panda.kassenbuch.data.entity.Buchung
import de.panda.kassenbuch.ui.theme.Ausgabe
import de.panda.kassenbuch.ui.theme.Einnahme
import de.panda.kassenbuch.util.Formatter
import kassenbuch.composeapp.generated.resources.Res
import kassenbuch.composeapp.generated.resources.all
import kassenbuch.composeapp.generated.resources.booking_new
import kassenbuch.composeapp.generated.resources.coin_counter_title
import kassenbuch.composeapp.generated.resources.dashboard_kassenbestand
import kassenbuch.composeapp.generated.resources.dashboard_month_expenses
import kassenbuch.composeapp.generated.resources.dashboard_month_revenue
import kassenbuch.composeapp.generated.resources.dashboard_no_bookings
import kassenbuch.composeapp.generated.resources.dashboard_quick_actions
import kassenbuch.composeapp.generated.resources.dashboard_recent
import kassenbuch.composeapp.generated.resources.dashboard_start_hint
import kassenbuch.composeapp.generated.resources.dashboard_title
import kassenbuch.composeapp.generated.resources.dashboard_today_expenses
import kassenbuch.composeapp.generated.resources.dashboard_today_revenue
import kassenbuch.composeapp.generated.resources.nav_bookings
import kassenbuch.composeapp.generated.resources.nav_info
import kassenbuch.composeapp.generated.resources.nav_settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    onNavigateToSettings: () -> Unit,
    onNavigateToNewBooking: () -> Unit
) {
    val screenModel = koinInject<DashboardScreenModel>()
    val state by screenModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = { /* info is a separate tab */ }) {
                        Icon(Icons.Filled.Info, contentDescription = stringResource(Res.string.nav_info))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(Res.string.nav_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewBooking,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(Res.string.booking_new)) },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Kassenbestand Card
                item {
                    KassenbestandCard(saldo = state.kassenbestand)
                }

                // Tages-Uebersicht
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.dashboard_today_revenue),
                            betrag = state.tagesEinnahmen,
                            color = Einnahme
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.dashboard_today_expenses),
                            betrag = state.tagesAusgaben,
                            color = Ausgabe
                        )
                    }
                }

                // Monats-Uebersicht
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.dashboard_month_revenue),
                            betrag = state.monatsEinnahmen,
                            color = Einnahme
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.dashboard_month_expenses),
                            betrag = state.monatsAusgaben,
                            color = Ausgabe
                        )
                    }
                }

                // Schnellaktionen
                item {
                    Text(
                        stringResource(Res.string.dashboard_quick_actions),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionChip(
                            icon = Icons.Filled.Add,
                            label = stringResource(Res.string.nav_bookings),
                            onClick = onNavigateToNewBooking,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionChip(
                            icon = Icons.Filled.Paid,
                            label = stringResource(Res.string.coin_counter_title),
                            onClick = { /* handled by daily tab */ },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionChip(
                            icon = Icons.Filled.Receipt,
                            label = stringResource(Res.string.all),
                            onClick = { /* handled by bookings tab */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Letzte Buchungen
                if (state.letzteBuchungen.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(Res.string.dashboard_recent),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(state.letzteBuchungen) { buchung ->
                        BuchungListItem(buchung = buchung)
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.Receipt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    stringResource(Res.string.dashboard_no_bookings),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    stringResource(Res.string.dashboard_start_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Spacer for FAB
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
fun KassenbestandCard(saldo: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.dashboard_kassenbestand),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                Formatter.euro(saldo),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    betrag: Double,
    color: Color
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                Formatter.euro(betrag),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun QuickActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun BuchungListItem(buchung: Buchung) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (buchung.istEinnahme) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                contentDescription = null,
                tint = if (buchung.istEinnahme) Einnahme else Ausgabe,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    buchung.buchungstext,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    "${Formatter.datumKurz(buchung.datum)} ${Formatter.uhrzeit(buchung.uhrzeit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${if (buchung.istEinnahme) "+" else "\u2212"}${Formatter.euro(buchung.betrag)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (buchung.istEinnahme) Einnahme else Ausgabe
            )
        }
    }
}
