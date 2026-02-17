package de.panda.kassenbuch

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.panda.kassenbuch.navigation.*
import de.panda.kassenbuch.ui.components.*
import de.panda.kassenbuch.ui.components.LocalStatusPopup
import de.panda.kassenbuch.ui.components.StatusPopup
import de.panda.kassenbuch.ui.components.StatusPopupController
import de.panda.kassenbuch.ui.screens.booking.BookingFormContent
import de.panda.kassenbuch.ui.screens.booking.BookingListContent
import de.panda.kassenbuch.ui.screens.daily.CoinCounterContent
import de.panda.kassenbuch.ui.screens.daily.DailyContent
import de.panda.kassenbuch.ui.screens.dashboard.DashboardContent
import de.panda.kassenbuch.ui.screens.export.ExportContent
import de.panda.kassenbuch.ui.screens.database.DatabaseContent
import de.panda.kassenbuch.ui.screens.info.InfoContent
import de.panda.kassenbuch.ui.screens.settings.SettingsContent
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import de.panda.kassenbuch.ui.theme.KassenbuchTheme
import de.panda.kassenbuch.util.PrefsManager
import de.panda.kassenbuch.util.seedDemoDataIfEmpty
import kotlinx.coroutines.launch
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

val LocalThemeChanger = staticCompositionLocalOf<(String) -> Unit> { {} }

@Composable
fun App() {
    val prefs = koinInject<PrefsManager>()
    val repository = koinInject<KassenbuchRepository>()
    var themeMode by remember { mutableStateOf(prefs.themeMode) }

    // Demo-Daten beim ersten Start einfuegen
    LaunchedEffect(Unit) {
        launch { seedDemoDataIfEmpty(repository) }
    }

    val statusPopupController = koinInject<StatusPopupController>()

    CompositionLocalProvider(
        LocalThemeChanger provides { themeMode = it },
        LocalStatusPopup provides statusPopupController
    ) {
        KassenbuchTheme(themeMode = themeMode) {
            val navController = rememberNavController()

            StatusPopup(statusPopupController)

            NavHost(
                navController = navController,
                startDestination = HomeRoute
            ) {
                composable<HomeRoute> {
                    HomeContent(
                        onNavigateToSettings = { navController.navigate(SettingsRoute) },
                        onNavigateToBookingForm = { id -> navController.navigate(BookingFormRoute(id)) },
                        onNavigateToCoinCounter = { navController.navigate(CoinCounterRoute) }
                    )
                }
                composable<SettingsRoute> {
                    SettingsContent(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<BookingFormRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<BookingFormRoute>()
                    BookingFormContent(
                        bookingId = route.bookingId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable<CoinCounterRoute> {
                    CoinCounterContent(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    onNavigateToSettings: () -> Unit,
    onNavigateToBookingForm: (Long?) -> Unit,
    onNavigateToCoinCounter: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabItems = listOf(
        TabItem(Res.string.nav_dashboard, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        TabItem(Res.string.nav_bookings, Icons.Filled.Receipt, Icons.Outlined.Receipt),
        TabItem(Res.string.nav_daily, Icons.Filled.Summarize, Icons.Outlined.Summarize),
        TabItem(Res.string.nav_export, Icons.Filled.FileDownload, Icons.Outlined.FileDownload),
        TabItem(Res.string.nav_database, Icons.Filled.Storage, Icons.Outlined.Storage),
        TabItem(Res.string.nav_info, Icons.Filled.Info, Icons.Outlined.Info)
    )

    BoxWithConstraints {
        val sizeClass = currentWindowSizeClass()

        when (sizeClass) {
            WindowSizeClass.Compact -> {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            tabItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = {
                                        Icon(
                                            if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(item.titleRes)) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        TabContent(
                            selectedTab = selectedTab,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToBookingForm = onNavigateToBookingForm,
                            onNavigateToCoinCounter = onNavigateToCoinCounter
                        )
                    }
                }
            }

            WindowSizeClass.Medium, WindowSizeClass.Expanded -> {
                Row(Modifier.fillMaxSize()) {
                    NavigationRail {
                        Spacer(Modifier.weight(1f))
                        tabItems.forEachIndexed { index, item ->
                            NavigationRailItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = {
                                    Icon(
                                        if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = null
                                    )
                                },
                                label = { Text(stringResource(item.titleRes)) }
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                    Box(Modifier.weight(1f)) {
                        TabContent(
                            selectedTab = selectedTab,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToBookingForm = onNavigateToBookingForm,
                            onNavigateToCoinCounter = onNavigateToCoinCounter
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabContent(
    selectedTab: Int,
    onNavigateToSettings: () -> Unit,
    onNavigateToBookingForm: (Long?) -> Unit,
    onNavigateToCoinCounter: () -> Unit
) {
    when (selectedTab) {
        0 -> DashboardContent(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToNewBooking = { onNavigateToBookingForm(null) }
        )
        1 -> BookingListContent(
            onNavigateToEdit = { id -> onNavigateToBookingForm(id) },
            onNavigateToNew = { onNavigateToBookingForm(null) }
        )
        2 -> DailyContent()
        3 -> ExportContent()
        4 -> DatabaseContent()
        5 -> InfoContent()
    }
}

private data class TabItem(
    val titleRes: StringResource,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
