package de.panda.kassenbuch.ui.screens.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.panda.kassenbuch.util.DatevExporter
import de.panda.kassenbuch.util.currentDate
import de.panda.kassenbuch.util.firstDayOfMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class ExportState(
    val von: LocalDate = currentDate().firstDayOfMonth(),
    val bis: LocalDate = currentDate(),
    val isExporting: Boolean = false,
    val exportedFilePath: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ExportScreenModel(
    private val exporter: DatevExporter
) : ViewModel() {

    private val _state = MutableStateFlow(ExportState())
    val state: StateFlow<ExportState> = _state.asStateFlow()

    fun updateVon(date: LocalDate) {
        _state.update { it.copy(von = date) }
    }

    fun updateBis(date: LocalDate) {
        _state.update { it.copy(bis = date) }
    }

    fun exportDatev() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isExporting = true,
                    errorMessage = null,
                    successMessage = null,
                    exportedFilePath = null
                )
            }
            try {
                val filePath = exporter.exportDatevCsv(_state.value.von, _state.value.bis)
                val fileName = filePath.substringAfterLast('/')
                    .substringAfterLast('\\')
                _state.update {
                    it.copy(
                        isExporting = false,
                        exportedFilePath = filePath,
                        successMessage = "DATEV Export erfolgreich: $fileName"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = e.message ?: "error_prefix"
                    )
                }
            }
        }
    }

    fun exportSimpleCsv() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isExporting = true,
                    errorMessage = null,
                    successMessage = null,
                    exportedFilePath = null
                )
            }
            try {
                val filePath = exporter.exportSimpleCsv(_state.value.von, _state.value.bis)
                val fileName = filePath.substringAfterLast('/')
                    .substringAfterLast('\\')
                _state.update {
                    it.copy(
                        isExporting = false,
                        exportedFilePath = filePath,
                        successMessage = "CSV Export erfolgreich: $fileName"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = e.message ?: "error_prefix"
                    )
                }
            }
        }
    }
}
