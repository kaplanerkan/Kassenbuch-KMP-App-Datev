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
actual fun PlatformInfoContent(modifier: Modifier, language: String) {
    val htmlFiles = listOf("anleitung_de.html", "anleitung_tr.html", "kassenbuch-voll.html")
    var tempDir by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val dir = File(System.getProperty("java.io.tmpdir"), "kassenbuch-anleitung")
            if (!dir.exists()) dir.mkdirs()

            htmlFiles.forEach { name ->
                val resource = Thread.currentThread().contextClassLoader
                    .getResourceAsStream("html/$name")
                resource?.use { input ->
                    File(dir, name).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            tempDir = dir
        }
    }

    val anleitungFile = if (language == "tr") "anleitung_tr.html" else "anleitung_de.html"

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            if (language == "tr") "Kassenbuch Kullanım Kılavuzu" else "Kassenbuch Anleitung",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            if (language == "tr") "Kılavuz ve örnek Kassenbuch tarayıcıda açılır."
            else "Die Anleitung und das Kassenbuch-Beispiel werden im Browser geöffnet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    tempDir?.let {
                        try {
                            Desktop.getDesktop().browse(File(it, anleitungFile).toURI())
                        } catch (_: Exception) { }
                    }
                },
                enabled = tempDir != null
            ) {
                Text(if (language == "tr") "Kılavuzu aç" else "Anleitung öffnen")
            }
            OutlinedButton(
                onClick = {
                    tempDir?.let {
                        try {
                            Desktop.getDesktop().browse(File(it, "kassenbuch-voll.html").toURI())
                        } catch (_: Exception) { }
                    }
                },
                enabled = tempDir != null
            ) {
                Text(if (language == "tr") "Örnek Kassenbuch" else "Kassenbuch-Beispiel")
            }
        }
    }
}
