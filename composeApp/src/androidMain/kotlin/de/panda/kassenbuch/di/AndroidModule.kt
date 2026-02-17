package de.panda.kassenbuch.di

import de.panda.kassenbuch.platform.DatabaseDriverFactory
import de.panda.kassenbuch.platform.PlatformDatabaseManager
import de.panda.kassenbuch.platform.PlatformFileWriter
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(get()) }
    single { PlatformFileWriter(get()) }
    single { PlatformDatabaseManager(get()) }
}
