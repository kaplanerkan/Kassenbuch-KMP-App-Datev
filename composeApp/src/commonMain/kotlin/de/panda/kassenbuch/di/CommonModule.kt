package de.panda.kassenbuch.di

import com.russhwolf.settings.Settings
import de.panda.kassenbuch.data.db.KassenbuchDatabase
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import de.panda.kassenbuch.platform.DatabaseDriverFactory
import de.panda.kassenbuch.ui.components.StatusPopupController
import de.panda.kassenbuch.ui.screens.booking.BookingScreenModel
import de.panda.kassenbuch.ui.screens.daily.DailyScreenModel
import de.panda.kassenbuch.ui.screens.dashboard.DashboardScreenModel
import de.panda.kassenbuch.ui.screens.database.DatabaseScreenModel
import de.panda.kassenbuch.ui.screens.export.ExportScreenModel
import de.panda.kassenbuch.util.DatevExporter
import de.panda.kassenbuch.util.PrefsManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    // Database
    single { get<DatabaseDriverFactory>().createDriver() }
    single { KassenbuchDatabase(get()) }

    // Repository
    single { KassenbuchRepository(get()) }

    // Settings
    single { Settings() }
    single { PrefsManager(get()) }

    // StatusPopup
    single { StatusPopupController() }

    // Exporter
    single { DatevExporter(get(), get(), get()) }

    // ScreenModels
    factory { DashboardScreenModel(get()) }
    factory { BookingScreenModel(get()) }
    factory { DailyScreenModel(get()) }
    factory { ExportScreenModel(get()) }
    factory { DatabaseScreenModel(get(), get()) }
}
