package de.panda.kassenbuch.data.entity

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Buchung(
    val id: Long = 0,
    val datum: LocalDate,
    val uhrzeit: LocalTime,
    val buchungsart: BuchungsArt,
    val betrag: Double,
    val buchungstext: String,
    val gegenkonto: Int,
    val buSchluessel: Int = 40,
    val steuersatz: Int = 0,
    val belegNr: String = "",
    val belegFotoUri: String? = null,
    val istEinnahme: Boolean,
    val tagesabschluss: Boolean = false,
    val notiz: String = "",
    val erstelltAm: Long = Clock.System.now().toEpochMilliseconds()
)
