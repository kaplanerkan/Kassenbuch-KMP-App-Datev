package de.panda.kassenbuch.di

import de.panda.kassenbuch.platform.DatabaseDriverFactory
import de.panda.kassenbuch.platform.PlatformFileWriter
import org.koin.dsl.module

val desktopModule = module {
    single { DatabaseDriverFactory() }
    single { PlatformFileWriter() }
}
