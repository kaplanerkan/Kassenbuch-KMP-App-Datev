package de.panda.kassenbuch.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.panda.kassenbuch.data.entity.Buchung
import de.panda.kassenbuch.data.entity.BuchungsArt
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import de.panda.kassenbuch.util.currentDate
import de.panda.kassenbuch.util.currentTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class BookingFormState(
    val id: Long = 0,
    val datum: LocalDate = currentDate(),
    val uhrzeit: LocalTime = currentTime(),
    val buchungsart: BuchungsArt = BuchungsArt.TAGESUMSATZ_7,
    val betrag: String = "",
    val buchungstext: String = "",
    val gegenkonto: Int = 8100,
    val buSchluessel: Int = 2,
    val steuersatz: Int = 7,
    val belegNr: String = "",
    val belegFotoUri: String? = null,
    val notiz: String = "",
    val istNeu: Boolean = true,
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class BookingListState(
    val buchungen: List<Buchung> = emptyList(),
    val filterDatum: LocalDate = currentDate(),
    val isLoading: Boolean = true
)

class BookingScreenModel(
    private val repository: KassenbuchRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(BookingFormState())
    val formState: StateFlow<BookingFormState> = _formState.asStateFlow()

    private val _listState = MutableStateFlow(BookingListState())
    val listState: StateFlow<BookingListState> = _listState.asStateFlow()

    private var listJob: Job? = null

    init {
        loadBuchungenForToday()
    }

    fun loadBuchungenForToday() {
        loadBuchungenForDate(currentDate())
    }

    fun loadBuchungenForDate(datum: LocalDate) {
        listJob?.cancel()
        listJob = viewModelScope.launch {
            _listState.update { it.copy(filterDatum = datum, isLoading = true) }
            repository.buchungenFuerTagFlow(datum).collect { buchungen ->
                _listState.update { it.copy(buchungen = buchungen, isLoading = false) }
            }
        }
    }

    fun loadBuchung(id: Long) {
        viewModelScope.launch {
            val buchung = repository.buchungFinden(id) ?: return@launch
            _formState.update {
                BookingFormState(
                    id = buchung.id,
                    datum = buchung.datum,
                    uhrzeit = buchung.uhrzeit,
                    buchungsart = buchung.buchungsart,
                    betrag = buchung.betrag.toString(),
                    buchungstext = buchung.buchungstext,
                    gegenkonto = buchung.gegenkonto,
                    buSchluessel = buchung.buSchluessel,
                    steuersatz = buchung.steuersatz,
                    belegNr = buchung.belegNr,
                    belegFotoUri = buchung.belegFotoUri,
                    notiz = buchung.notiz,
                    istNeu = false
                )
            }
        }
    }

    fun resetForm() {
        viewModelScope.launch {
            val heute = currentDate()
            val nextBelegNr = repository.naechsteBelegNr(heute)
            _formState.value = BookingFormState(
                datum = heute,
                uhrzeit = currentTime(),
                belegNr = "B-${nextBelegNr.toString().padStart(3, '0')}"
            )
        }
    }

    fun updateBuchungsart(art: BuchungsArt) {
        _formState.update {
            it.copy(
                buchungsart = art,
                gegenkonto = art.defaultGegenkonto,
                buSchluessel = art.defaultBuSchluessel,
                steuersatz = art.defaultSteuersatz,
                buchungstext = if (it.buchungstext.isEmpty()) art.labelKey else it.buchungstext
            )
        }
    }

    fun updateBetrag(betrag: String) {
        _formState.update { it.copy(betrag = betrag) }
    }

    fun updateBuchungstext(text: String) {
        _formState.update { it.copy(buchungstext = text) }
    }

    fun updateDatum(datum: LocalDate) {
        _formState.update { it.copy(datum = datum) }
    }

    fun updateUhrzeit(uhrzeit: LocalTime) {
        _formState.update { it.copy(uhrzeit = uhrzeit) }
    }

    fun updateGegenkonto(konto: Int) {
        _formState.update { it.copy(gegenkonto = konto) }
    }

    fun updateBelegNr(nr: String) {
        _formState.update { it.copy(belegNr = nr) }
    }

    fun updateBelegFoto(uri: String?) {
        _formState.update { it.copy(belegFotoUri = uri) }
    }

    fun updateNotiz(notiz: String) {
        _formState.update { it.copy(notiz = notiz) }
    }

    fun save() {
        val form = _formState.value
        val betrag = form.betrag.replace(",", ".").toDoubleOrNull()

        if (betrag == null || betrag <= 0) {
            _formState.update { it.copy(errorMessage = "booking_error_amount") }
            return
        }
        if (form.buchungstext.isBlank()) {
            _formState.update { it.copy(errorMessage = "booking_error_text") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                val buchung = Buchung(
                    id = if (form.istNeu) 0 else form.id,
                    datum = form.datum,
                    uhrzeit = form.uhrzeit,
                    buchungsart = form.buchungsart,
                    betrag = betrag,
                    buchungstext = form.buchungstext,
                    gegenkonto = form.gegenkonto,
                    buSchluessel = form.buSchluessel,
                    steuersatz = form.steuersatz,
                    belegNr = form.belegNr,
                    belegFotoUri = form.belegFotoUri,
                    istEinnahme = form.buchungsart.einnahme,
                    notiz = form.notiz
                )

                if (form.istNeu) {
                    repository.buchungSpeichern(buchung)
                } else {
                    repository.buchungAktualisieren(buchung)
                }

                _formState.update { it.copy(isSaving = false, savedSuccess = true) }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "error_prefix")
                }
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repository.buchungLoeschen(id)
        }
    }
}
