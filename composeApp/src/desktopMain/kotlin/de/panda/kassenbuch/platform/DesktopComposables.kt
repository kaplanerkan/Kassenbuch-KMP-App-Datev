package de.panda.kassenbuch.platform

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

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
                val chooser = JFileChooser().apply {
                    dialogTitle = "Belegfoto auswählen"
                    fileFilter = FileNameExtensionFilter(
                        "Bilder (JPG, PNG)",
                        "jpg", "jpeg", "png"
                    )
                }
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    onPhotoTaken(chooser.selectedFile.absolutePath)
                }
            }) {
                Icon(Icons.Filled.FolderOpen, null)
                Spacer(Modifier.width(4.dp))
                Text("Bild wählen")
            }
        }
        if (currentPhotoUri != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                currentPhotoUri,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            try {
                Desktop.getDesktop().open(File(filePath))
            } catch (_: Exception) { }
        },
        modifier = modifier
    ) {
        Icon(Icons.Filled.Share, null)
        Spacer(Modifier.width(4.dp))
        Text("Öffnen")
    }
}

@Composable
actual fun PlatformInfoContent(modifier: Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Kassenbuch Anleitung",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Die Anleitung wird im Desktop-Browser geöffnet.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            try {
                val uri = java.net.URI("https://kassenbuch.de/anleitung")
                Desktop.getDesktop().browse(uri)
            } catch (_: Exception) { }
        }) {
            Text("Anleitung öffnen")
        }
    }
}
