package de.panda.kassenbuch.data.entity

import kotlin.time.Clock
import kotlinx.datetime.LocalDate

data class Tagesabschluss(
    val datum: LocalDate,
    val vortrag: Double,
    val summeEinnahmen: Double,
    val summeAusgaben: Double,
    val endbestand: Double,
    val zBerichtNr: String = "",
    val gezaehlterBestand: Double? = null,
    val differenz: Double? = null,
    val geschlossen: Boolean = false,
    val notiz: String = "",
    val erstelltAm: Long = Clock.System.now().toEpochMilliseconds()
)
