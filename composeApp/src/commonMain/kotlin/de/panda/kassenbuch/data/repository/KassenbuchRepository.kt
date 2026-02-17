package de.panda.kassenbuch.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import de.panda.kassenbuch.data.db.KassenbuchDatabase
import de.panda.kassenbuch.data.entity.Buchung
import de.panda.kassenbuch.data.entity.BuchungsArt
import de.panda.kassenbuch.data.entity.Tagesabschluss
import de.panda.kassenbuch.util.minusDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Kassenbuch Repository -- zentrale Datenzugriffsschicht.
 *
 * Verwendet SQLDelight statt Room und stellt alle
 * Datenbankoperationen den ViewModels bereit.
 */
class KassenbuchRepository(
    private val database: KassenbuchDatabase
) {
    private val buchungenQueries = database.buchungenQueries
    private val tagesabschluesseQueries = database.tagesabschluesseQueries

    // ══════════════════════════════════════
    // Mapper: SQLDelight Row -> Domain Model
    // ══════════════════════════════════════

    private fun de.panda.kassenbuch.Buchungen.toDomain(): Buchung = Buchung(
        id = id,
        datum = LocalDate.parse(datum),
        uhrzeit = LocalTime.parse(uhrzeit),
        buchungsart = BuchungsArt.valueOf(buchungsart),
        betrag = betrag,
        buchungstext = buchungstext,
        gegenkonto = gegenkonto.toInt(),
        buSchluessel = buSchluessel.toInt(),
        steuersatz = steuersatz.toInt(),
        belegNr = belegNr,
        belegFotoUri = belegFotoUri,
        istEinnahme = istEinnahme != 0L,
        tagesabschluss = tagesabschluss != 0L,
        notiz = notiz,
        erstelltAm = erstelltAm
    )

    private fun de.panda.kassenbuch.Tagesabschluesse.toDomain(): Tagesabschluss = Tagesabschluss(
        datum = LocalDate.parse(datum),
        vortrag = vortrag,
        summeEinnahmen = summeEinnahmen,
        summeAusgaben = summeAusgaben,
        endbestand = endbestand,
        zBerichtNr = zBerichtNr,
        gezaehlterBestand = gezaehlterBestand,
        differenz = differenz,
        geschlossen = geschlossen != 0L,
        notiz = notiz,
        erstelltAm = erstelltAm
    )

    // ══════════════════════════════════════
    // Buchungen -- Flow-Abfragen
    // ══════════════════════════════════════

    fun alleBuchungenFlow(): Flow<List<Buchung>> =
        buchungenQueries.alle()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }

    fun buchungenFuerTagFlow(datum: LocalDate): Flow<List<Buchung>> =
        buchungenQueries.fuerTag(datum.toString())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }

    fun buchungenFuerZeitraumFlow(von: LocalDate, bis: LocalDate): Flow<List<Buchung>> =
        buchungenQueries.fuerZeitraum(von.toString(), bis.toString())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }

    // ══════════════════════════════════════
    // Buchungen -- Suspend-Abfragen
    // ══════════════════════════════════════

    suspend fun buchungenFuerZeitraum(von: LocalDate, bis: LocalDate): List<Buchung> =
        withContext(Dispatchers.Default) {
            buchungenQueries.fuerZeitraum(von.toString(), bis.toString())
                .executeAsList()
                .map { it.toDomain() }
        }

    suspend fun buchungenFuerTag(datum: LocalDate): List<Buchung> =
        withContext(Dispatchers.Default) {
            buchungenQueries.fuerTag(datum.toString())
                .executeAsList()
                .map { it.toDomain() }
        }

    suspend fun buchungFinden(id: Long): Buchung? =
        withContext(Dispatchers.Default) {
            buchungenQueries.findById(id)
                .executeAsOneOrNull()
                ?.toDomain()
        }

    // ══════════════════════════════════════
    // Buchungen -- Schreiboperationen
    // ══════════════════════════════════════

    suspend fun buchungSpeichern(buchung: Buchung): Long =
        withContext(Dispatchers.Default) {
            buchungenQueries.transactionWithResult {
                buchungenQueries.insertBuchung(
                    datum = buchung.datum.toString(),
                    uhrzeit = buchung.uhrzeit.toString(),
                    buchungsart = buchung.buchungsart.name,
                    betrag = buchung.betrag,
                    buchungstext = buchung.buchungstext,
                    gegenkonto = buchung.gegenkonto.toLong(),
                    buSchluessel = buchung.buSchluessel.toLong(),
                    steuersatz = buchung.steuersatz.toLong(),
                    belegNr = buchung.belegNr,
                    belegFotoUri = buchung.belegFotoUri,
                    istEinnahme = if (buchung.istEinnahme) 1L else 0L,
                    tagesabschluss = if (buchung.tagesabschluss) 1L else 0L,
                    notiz = buchung.notiz,
                    erstelltAm = buchung.erstelltAm
                )
                buchungenQueries.lastInsertId().executeAsOne()
            }
        }

    suspend fun buchungAktualisieren(buchung: Buchung) =
        withContext(Dispatchers.Default) {
            buchungenQueries.updateBuchung(
                datum = buchung.datum.toString(),
                uhrzeit = buchung.uhrzeit.toString(),
                buchungsart = buchung.buchungsart.name,
                betrag = buchung.betrag,
                buchungstext = buchung.buchungstext,
                gegenkonto = buchung.gegenkonto.toLong(),
                buSchluessel = buchung.buSchluessel.toLong(),
                steuersatz = buchung.steuersatz.toLong(),
                belegNr = buchung.belegNr,
                belegFotoUri = buchung.belegFotoUri,
                istEinnahme = if (buchung.istEinnahme) 1L else 0L,
                tagesabschluss = if (buchung.tagesabschluss) 1L else 0L,
                notiz = buchung.notiz,
                erstelltAm = buchung.erstelltAm,
                id = buchung.id
            )
        }

    suspend fun buchungLoeschen(id: Long) =
        withContext(Dispatchers.Default) {
            buchungenQueries.deleteById(id)
        }

    // ══════════════════════════════════════
    // Kassenbestand & Berechnungen
    // ══════════════════════════════════════

    suspend fun kassenbestandBis(datum: LocalDate): Double =
        withContext(Dispatchers.Default) {
            val datumStr = datum.toString()
            buchungenQueries.kassenbestandBis(datumStr, datumStr)
                .executeAsOne()
        }

    suspend fun tagesEinnahmen(datum: LocalDate): Double =
        withContext(Dispatchers.Default) {
            buchungenQueries.tagesEinnahmen(datum.toString())
                .executeAsOne()
        }

    suspend fun tagesAusgaben(datum: LocalDate): Double =
        withContext(Dispatchers.Default) {
            buchungenQueries.tagesAusgaben(datum.toString())
                .executeAsOne()
        }

    suspend fun zeitraumEinnahmen(von: LocalDate, bis: LocalDate): Double =
        withContext(Dispatchers.Default) {
            buchungenQueries.zeitraumEinnahmen(von.toString(), bis.toString())
                .executeAsOne()
        }

    suspend fun zeitraumAusgaben(von: LocalDate, bis: LocalDate): Double =
        withContext(Dispatchers.Default) {
            buchungenQueries.zeitraumAusgaben(von.toString(), bis.toString())
                .executeAsOne()
        }

    suspend fun naechsteBelegNr(datum: LocalDate): Int =
        withContext(Dispatchers.Default) {
            buchungenQueries.naechsteBelegNr(datum.toString())
                .executeAsOne()
                .toInt()
        }

    suspend fun gesamtAnzahl(): Long =
        withContext(Dispatchers.Default) {
            buchungenQueries.gesamtAnzahl().executeAsOne()
        }

    suspend fun alleTagesMitBuchungen(): List<LocalDate> =
        withContext(Dispatchers.Default) {
            buchungenQueries.alleTagesMitBuchungen()
                .executeAsList()
                .map { LocalDate.parse(it) }
        }

    // ══════════════════════════════════════
    // Vortrag berechnen
    // ══════════════════════════════════════

    /**
     * Berechnet den Vortrag (Anfangsbestand) fuer einen Tag.
     * = Kassenbestand bis zum Vortag
     */
    suspend fun vortragFuerTag(datum: LocalDate): Double {
        val vortag = datum.minusDays(1)
        return kassenbestandBis(vortag)
    }

    // ══════════════════════════════════════
    // Tagesabschluss
    // ══════════════════════════════════════

    fun alleTagesabschluesseFlow(): Flow<List<Tagesabschluss>> =
        tagesabschluesseQueries.alle()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }

    suspend fun tagesabschlussFuerTag(datum: LocalDate): Tagesabschluss? =
        withContext(Dispatchers.Default) {
            tagesabschluesseQueries.fuerTag(datum.toString())
                .executeAsOneOrNull()
                ?.toDomain()
        }

    suspend fun tagesabschlussSpeichern(abschluss: Tagesabschluss) =
        withContext(Dispatchers.Default) {
            tagesabschluesseQueries.insertAbschluss(
                datum = abschluss.datum.toString(),
                vortrag = abschluss.vortrag,
                summeEinnahmen = abschluss.summeEinnahmen,
                summeAusgaben = abschluss.summeAusgaben,
                endbestand = abschluss.endbestand,
                zBerichtNr = abschluss.zBerichtNr,
                gezaehlterBestand = abschluss.gezaehlterBestand,
                differenz = abschluss.differenz,
                geschlossen = if (abschluss.geschlossen) 1L else 0L,
                notiz = abschluss.notiz,
                erstelltAm = abschluss.erstelltAm
            )
        }

    suspend fun istTagAbgeschlossen(datum: LocalDate): Boolean =
        withContext(Dispatchers.Default) {
            tagesabschluesseQueries.existiert(datum.toString())
                .executeAsOne()
        }

    /**
     * Erstellt einen Tagesabschluss fuer den angegebenen Tag.
     * Alle Lese- und Schreiboperationen werden in einer Transaktion ausgefuehrt.
     */
    suspend fun tagesabschlussErstellen(
        datum: LocalDate,
        zBerichtNr: String = "",
        gezaehlterBestand: Double? = null,
        notiz: String = ""
    ): Tagesabschluss = withContext(Dispatchers.Default) {
        database.transactionWithResult {
            val vortrag = vortragFuerTagSync(datum)
            val einnahmen = tagesEinnahmenSync(datum)
            val ausgaben = tagesAusgabenSync(datum)
            val endbestand = vortrag + einnahmen - ausgaben
            val differenz = gezaehlterBestand?.let { it - endbestand }

            val abschluss = Tagesabschluss(
                datum = datum,
                vortrag = vortrag,
                summeEinnahmen = einnahmen,
                summeAusgaben = ausgaben,
                endbestand = endbestand,
                zBerichtNr = zBerichtNr,
                gezaehlterBestand = gezaehlterBestand,
                differenz = differenz,
                notiz = notiz
            )

            tagesabschluesseQueries.insertAbschluss(
                datum = abschluss.datum.toString(),
                vortrag = abschluss.vortrag,
                summeEinnahmen = abschluss.summeEinnahmen,
                summeAusgaben = abschluss.summeAusgaben,
                endbestand = abschluss.endbestand,
                zBerichtNr = abschluss.zBerichtNr,
                gezaehlterBestand = abschluss.gezaehlterBestand,
                differenz = abschluss.differenz,
                geschlossen = if (abschluss.geschlossen) 1L else 0L,
                notiz = abschluss.notiz,
                erstelltAm = abschluss.erstelltAm
            )

            abschluss
        }
    }

    // ══════════════════════════════════════
    // Synchrone Helfer (fuer Transaktionen)
    // ══════════════════════════════════════

    private fun vortragFuerTagSync(datum: LocalDate): Double {
        val vortag = datum.minusDays(1)
        return kassenbestandBisSync(vortag)
    }

    private fun kassenbestandBisSync(datum: LocalDate): Double {
        val datumStr = datum.toString()
        return buchungenQueries.kassenbestandBis(datumStr, datumStr)
            .executeAsOne()
    }

    private fun tagesEinnahmenSync(datum: LocalDate): Double =
        buchungenQueries.tagesEinnahmen(datum.toString())
            .executeAsOne()

    private fun tagesAusgabenSync(datum: LocalDate): Double =
        buchungenQueries.tagesAusgaben(datum.toString())
            .executeAsOne()
}
