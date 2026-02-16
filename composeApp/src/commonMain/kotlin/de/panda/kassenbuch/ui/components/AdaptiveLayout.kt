package de.panda.kassenbuch.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    Compact,  // < 600dp - Phone
    Medium,   // 600-840dp - Tablet
    Expanded  // > 840dp - Desktop
}

@Composable
fun BoxWithConstraintsScope.currentWindowSizeClass(): WindowSizeClass = when {
    maxWidth < 600.dp -> WindowSizeClass.Compact
    maxWidth < 840.dp -> WindowSizeClass.Medium
    else -> WindowSizeClass.Expanded
}

fun adaptiveHorizontalPadding(sizeClass: WindowSizeClass): Dp = when (sizeClass) {
    WindowSizeClass.Compact -> 16.dp
    WindowSizeClass.Medium -> 24.dp
    WindowSizeClass.Expanded -> 32.dp
}

fun adaptiveColumns(sizeClass: WindowSizeClass): Int = when (sizeClass) {
    WindowSizeClass.Compact -> 1
    WindowSizeClass.Medium -> 2
    WindowSizeClass.Expanded -> 3
}
