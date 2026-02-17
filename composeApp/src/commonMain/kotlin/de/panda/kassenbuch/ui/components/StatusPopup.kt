package de.panda.kassenbuch.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StatusType(val icon: ImageVector, val color: Color, val titleText: String) {
    INFO(Icons.Filled.Info, Color(0xFF1976D2), "Information"),
    SUCCESS(Icons.Filled.CheckCircle, Color(0xFF388E3C), "Erfolg"),
    WARNING(Icons.Filled.Warning, Color(0xFFF57C00), "Warnung"),
    ERROR(Icons.Filled.Error, Color(0xFFD32F2F), "Fehler")
}

data class StatusPopupState(
    val message: String = "",
    val type: StatusType = StatusType.INFO,
    val isVisible: Boolean = false
)

class StatusPopupController {
    private val _state = MutableStateFlow(StatusPopupState())
    val state: StateFlow<StatusPopupState> = _state.asStateFlow()

    private var autoDismissJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun showInfo(message: String) = show(message, StatusType.INFO)
    fun showSuccess(message: String) = show(message, StatusType.SUCCESS)
    fun showWarning(message: String) = show(message, StatusType.WARNING)
    fun showError(message: String) = show(message, StatusType.ERROR)

    fun dismiss() {
        autoDismissJob?.cancel()
        _state.value = _state.value.copy(isVisible = false)
    }

    private fun show(message: String, type: StatusType) {
        autoDismissJob?.cancel()
        _state.value = StatusPopupState(message = message, type = type, isVisible = true)

        if (type == StatusType.INFO || type == StatusType.SUCCESS) {
            autoDismissJob = scope.launch {
                delay(3000)
                dismiss()
            }
        }
    }
}

val LocalStatusPopup = staticCompositionLocalOf<StatusPopupController> {
    error("StatusPopupController not provided")
}

@Composable
fun StatusPopup(controller: StatusPopupController) {
    val popupState by controller.state.collectAsState()

    if (popupState.isVisible) {
        val type = popupState.type

        AlertDialog(
            onDismissRequest = { controller.dismiss() },
            icon = {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = type.color,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = type.titleText,
                    fontWeight = FontWeight.Bold,
                    color = type.color
                )
            },
            text = {
                Text(
                    text = popupState.message,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = { controller.dismiss() }) {
                    Text("OK")
                }
            }
        )
    }
}
