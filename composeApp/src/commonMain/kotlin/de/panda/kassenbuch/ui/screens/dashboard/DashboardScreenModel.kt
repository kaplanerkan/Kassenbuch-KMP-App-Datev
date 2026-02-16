package de.panda.kassenbuch.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.panda.kassenbuch.data.entity.Buchung
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import de.panda.kassenbuch.util.currentDate
import de.panda.kassenbuch.util.firstDayOfMonth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardState(
    val kassenbestand: Double = 0.0,
    val tagesEinnahmen: Double = 0.0,
    val tagesAusgaben: Double = 0.0,
    val monatsEinnahmen: Double = 0.0,
    val monatsAusgaben: Double = 0.0,
    val letzteBuchungen: List<Buchung> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardScreenModel(
    private val repository: KassenbuchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Flow collect -- wird bei jeder DB-Aenderung neu ausgeloest
            repository.alleBuchungenFlow().collect { alle ->
                val heute = currentDate()
                val monatsAnfang = heute.firstDayOfMonth()

                val kassenbestand = repository.kassenbestandBis(heute)
                val tagesEin = repository.tagesEinnahmen(heute)
                val tagesAus = repository.tagesAusgaben(heute)
                val monatsEin = repository.zeitraumEinnahmen(monatsAnfang, heute)
                val monatsAus = repository.zeitraumAusgaben(monatsAnfang, heute)

                _state.update {
                    it.copy(
                        kassenbestand = kassenbestand,
                        tagesEinnahmen = tagesEin,
                        tagesAusgaben = tagesAus,
                        monatsEinnahmen = monatsEin,
                        monatsAusgaben = monatsAus,
                        letzteBuchungen = alle.take(10),
                        isLoading = false
                    )
                }
            }
        }
    }
}
