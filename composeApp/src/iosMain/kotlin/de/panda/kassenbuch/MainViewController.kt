package de.panda.kassenbuch

import androidx.compose.ui.window.ComposeUIViewController
import de.panda.kassenbuch.di.commonModule
import de.panda.kassenbuch.di.iosModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(commonModule, iosModule)
        }
    }
) {
    App()
}
