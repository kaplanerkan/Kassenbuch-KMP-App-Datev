package de.panda.kassenbuch

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import de.panda.kassenbuch.di.commonModule
import de.panda.kassenbuch.di.desktopModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(commonModule, desktopModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Kassenbuch",
            state = rememberWindowState(width = 1024.dp, height = 768.dp)
        ) {
            App()
        }
    }
}
