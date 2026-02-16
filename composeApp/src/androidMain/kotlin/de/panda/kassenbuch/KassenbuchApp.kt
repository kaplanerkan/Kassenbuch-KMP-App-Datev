package de.panda.kassenbuch

import android.app.Application
import de.panda.kassenbuch.di.androidModule
import de.panda.kassenbuch.di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KassenbuchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KassenbuchApp)
            modules(commonModule, androidModule)
        }
    }
}
