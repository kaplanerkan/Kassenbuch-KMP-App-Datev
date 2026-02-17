package de.panda.kassenbuch.platform

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import java.io.File

@Composable
actual fun platformColorScheme(darkTheme: Boolean): ColorScheme? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    } else null
}

@Composable
actual fun ReceiptPhotoCapture(
    currentPhotoUri: String?,
    onPhotoTaken: (String) -> Unit
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { onPhotoTaken(it.toString()) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            photoUri?.let { cameraLauncher.launch(it) }
        }
    }

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
                val dir = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "Belege"
                )
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "beleg_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                photoUri = uri
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Icon(Icons.Filled.CameraAlt, null)
                Spacer(Modifier.width(4.dp))
                Text("Foto")
            }
        }
        if (currentPhotoUri != null) {
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = currentPhotoUri,
                contentDescription = "Beleg-Foto",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
        }
    }
}

@Composable
actual fun PlatformShareButton(
    filePath: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    FilledTonalButton(
        onClick = {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                android.content.Intent.createChooser(intent, "Export teilen")
            )
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = null
        )
        Spacer(Modifier.width(4.dp))
        Text("Teilen")
    }
}

@Composable
actual fun PlatformInfoContent(modifier: Modifier, language: String) {
    val url = if (language == "tr") {
        "file:///android_asset/anleitung_tr.html"
    } else {
        "file:///android_asset/anleitung_de.html"
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}
