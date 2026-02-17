package de.panda.kassenbuch.ui.screens.info

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.panda.kassenbuch.platform.PlatformInfoContent
import kassenbuch.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoContent() {
    var selectedLanguage by remember { mutableStateOf("de") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.info_title)) },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = selectedLanguage == "de",
                            onClick = { selectedLanguage = "de" },
                            label = { Text("Deutsch") }
                        )
                        FilterChip(
                            selected = selectedLanguage == "tr",
                            onClick = { selectedLanguage = "tr" },
                            label = { Text("Türkçe") }
                        )
                    }
                }
            )
        }
    ) { padding ->
        PlatformInfoContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            language = selectedLanguage
        )
    }
}
