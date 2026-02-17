package de.panda.kassenbuch.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun platformColorScheme(darkTheme: Boolean): ColorScheme?

@Composable
expect fun ReceiptPhotoCapture(
    currentPhotoUri: String?,
    onPhotoTaken: (String) -> Unit
)

@Composable
expect fun PlatformShareButton(
    filePath: String,
    modifier: Modifier
)

@Composable
expect fun PlatformInfoContent(modifier: Modifier, language: String = "de")
