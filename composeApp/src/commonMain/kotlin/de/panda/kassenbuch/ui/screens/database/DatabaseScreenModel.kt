package de.panda.kassenbuch.ui.screens.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.panda.kassenbuch.platform.PlatformDatabaseManager
import de.panda.kassenbuch.ui.components.StatusPopupController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DatabaseState(
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val backupFiles: List<String> = emptyList(),
    val lastBackupPath: String? = null
)

class DatabaseScreenModel(
    private val dbManager: PlatformDatabaseManager,
    private val statusController: StatusPopupController
) : ViewModel() {

    private val _state = MutableStateFlow(DatabaseState())
    val state: StateFlow<DatabaseState> = _state.asStateFlow()

    init {
        loadBackupList()
    }

    fun loadBackupList() {
        _state.update { it.copy(backupFiles = dbManager.listBackups()) }
    }

    fun createBackup() {
        viewModelScope.launch {
            _state.update { it.copy(isBackingUp = true) }
            try {
                val path = dbManager.createBackup()
                _state.update {
                    it.copy(
                        isBackingUp = false,
                        lastBackupPath = path,
                        backupFiles = dbManager.listBackups()
                    )
                }
                statusController.showSuccess("Backup erfolgreich erstellt:\n${path.substringAfterLast('/')}")
            } catch (e: Exception) {
                _state.update { it.copy(isBackingUp = false) }
                statusController.showError("Backup fehlgeschlagen: ${e.message}")
            }
        }
    }

    fun restoreBackup(zipPath: String) {
        viewModelScope.launch {
            _state.update { it.copy(isRestoring = true) }
            try {
                dbManager.restoreFromBackup(zipPath)
                _state.update { it.copy(isRestoring = false) }
                statusController.showWarning("Datenbank wiederhergestellt.\nBitte App neu starten!")
            } catch (e: Exception) {
                _state.update { it.copy(isRestoring = false) }
                statusController.showError("Wiederherstellung fehlgeschlagen: ${e.message}")
            }
        }
    }
}
