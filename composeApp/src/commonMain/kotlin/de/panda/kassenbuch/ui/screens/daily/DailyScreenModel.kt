package de.panda.kassenbuch.ui.screens.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.panda.kassenbuch.data.entity.Tagesabschluss
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import de.panda.kassenbuch.util.currentDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class DailyState(
    val datum: LocalDate = currentDate(),
    val vortrag: Double = 0.0,
    val einnahmen: Double = 0.0,
    val ausgaben: Double = 0.0,
    val endbestand: Double = 0.0,
    val gezaehlterBestand: String = "",
    val differenz: Double? = null,
    val istAbgeschlossen: Boolean = false,
    val abschluss: Tagesabschluss? = null,
    val isLoading: Boolean = true
)

class DailyScreenModel(
    private val repository: KassenbuchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyState())
    val state: StateFlow<DailyState> = _state.asStateFlow()

    init {
        loadTag(currentDate())
    }

    fun loadTag(datum: LocalDate) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    datum = datum,
                    isLoading = true,
                    gezaehlterBestand = "",
                    differenz = null
                )
            }

            val vortrag = repository.vortragFuerTag(datum)
            val einnahmen = repository.tagesEinnahmen(datum)
            val ausgaben = repository.tagesAusgaben(datum)
            val endbestand = vortrag + einnahmen - ausgaben
            val abgeschlossen = repository.istTagAbgeschlossen(datum)
            val abschluss = repository.tagesabschlussFuerTag(datum)

            _state.update {
                it.copy(
                    vortrag = vortrag,
                    einnahmen = einnahmen,
                    ausgaben = ausgaben,
                    endbestand = endbestand,
                    istAbgeschlossen = abgeschlossen,
                    abschluss = abschluss,
                    isLoading = false
                )
            }
        }
    }

    fun updateGezaehlterBestand(value: String) {
        _state.update {
            val gezaehlt = value.replace(",", ".").toDoubleOrNull()
            it.copy(
                gezaehlterBestand = value,
                differenz = gezaehlt?.let { v -> v - it.endbestand }
            )
        }
    }

    fun tagesabschlussDurchfuehren() {
        viewModelScope.launch {
            val s = _state.value
            val gezaehlt = s.gezaehlterBestand.replace(",", ".").toDoubleOrNull()

            repository.tagesabschlussErstellen(
                datum = s.datum,
                gezaehlterBestand = gezaehlt
            )

            _state.update { it.copy(istAbgeschlossen = true) }
        }
    }
}
