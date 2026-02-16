package de.panda.kassenbuch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.panda.kassenbuch.platform.platformColorScheme

// ── Farben ──
val KasseGreen = Color(0xFF2D6A4F)
val KasseGreenLight = Color(0xFF40916C)
val KasseGreenContainer = Color(0xFFB7E4C7)
val Einnahme = Color(0xFF34D399)
val Ausgabe = Color(0xFFF87171)
val SaldoBlue = Color(0xFF4F8CFF)
val WechselgeldGelb = Color(0xFFFBBF24)
val GutscheinLila = Color(0xFFA855F7)

private val LightColors = lightColorScheme(
    primary = KasseGreen,
    onPrimary = Color.White,
    primaryContainer = KasseGreenContainer,
    onPrimaryContainer = Color(0xFF002110),
    secondary = KasseGreenLight,
    onSecondary = Color.White,
    tertiary = SaldoBlue,
    background = Color(0xFFF8FAF8),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F4F0),
    onBackground = Color(0xFF1A1C1A),
    onSurface = Color(0xFF1A1C1A),
    error = Ausgabe
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6BC99D),
    onPrimary = Color(0xFF003822),
    primaryContainer = KasseGreen,
    onPrimaryContainer = KasseGreenContainer,
    secondary = Color(0xFF83D5AA),
    onSecondary = Color(0xFF003822),
    tertiary = SaldoBlue,
    background = Color(0xFF0F1117),
    surface = Color(0xFF1A1D27),
    surfaceVariant = Color(0xFF232734),
    onBackground = Color(0xFFE4E7F0),
    onSurface = Color(0xFFE4E7F0),
    error = Ausgabe
)

@Composable
fun KassenbuchTheme(
    themeMode: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    // Try platform-specific dynamic colors first (Android 12+ Material You)
    val dynamicScheme: ColorScheme? = platformColorScheme(darkTheme)

    val colorScheme = dynamicScheme ?: if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
