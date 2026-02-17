package de.panda.kassenbuch.platform

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun platformColorScheme(darkTheme: Boolean): ColorScheme? = null

@Composable
actual fun ReceiptPhotoCapture(
    currentPhotoUri: String?,
    onPhotoTaken: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Beleg-Foto",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    if (currentPhotoUri != null) "Foto vorhanden" else "Kein Foto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledTonalButton(onClick = {
                // TODO: iOS camera integration via UIImagePickerController
            }) {
                Icon(Icons.Filled.FolderOpen, null)
                Spacer(Modifier.width(4.dp))
                Text("Foto")
            }
        }
    }
}

@Composable
actual fun PlatformShareButton(
    filePath: String,
    modifier: Modifier
) {
    FilledTonalButton(
        onClick = {
            // TODO: iOS share via UIActivityViewController
        },
        modifier = modifier
    ) {
        Icon(Icons.Filled.Share, null)
        Spacer(Modifier.width(4.dp))
        Text("Teilen")
    }
}

@Composable
actual fun PlatformInfoContent(modifier: Modifier, language: String) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            if (language == "tr") "Kassenbuch Kullanım Kılavuzu" else "Kassenbuch Anleitung",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        Text(
            if (language == "tr") "Kılavuz henüz iOS için hazır değil."
            else "Die Anleitung ist für iOS noch nicht verfügbar.",
            style = MaterialTheme.typography.bodyMedium
        )
        // TODO: WKWebView integration for iOS
    }
}
