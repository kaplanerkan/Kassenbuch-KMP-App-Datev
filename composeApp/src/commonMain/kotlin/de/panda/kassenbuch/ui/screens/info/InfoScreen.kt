package de.panda.kassenbuch.ui.screens.info

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.panda.kassenbuch.platform.PlatformInfoContent
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.info_title)) }
            )
        }
    ) { padding ->
        PlatformInfoContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
