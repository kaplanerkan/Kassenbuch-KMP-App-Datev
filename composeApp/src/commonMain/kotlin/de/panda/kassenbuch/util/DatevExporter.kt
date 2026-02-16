package de.panda.kassenbuch.util

import de.panda.kassenbuch.data.entity.Buchung
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import de.panda.kassenbuch.platform.PlatformFileWriter
import kotlinx.datetime.LocalDate

/**
 * DATEV Buchungsstapel Exporter (Multiplatform).
 *
 * Exportiert Kassenbuch-Daten im DATEV-kompatiblen CSV-Format
 * (EXTF Formatbeschreibung v12.0) fuer die Uebergabe an den
 * Steuerberater.
 *
 * Ausserdem: einfaches Kassenbuch-CSV.
 *
 * Die CSV-Inhalte werden als reine Strings erzeugt (kein File-I/O).
 * Das eigentliche Schreiben uebernimmt [PlatformFileWriter].
 */
class DatevExporter(
    private val repository: KassenbuchRepository,
    private val prefs: PrefsManager,
    private val fileWriter: PlatformFileWriter
) {

    // ====================================================
    // DATEV Buchungsstapel CSV
    // ====================================================

    /**
     * Erzeugt eine DATEV Buchungsstapel CSV-Datei und gibt
     * den Dateipfad zurueck.
     */
    suspend fun exportDatevCsv(von: LocalDate, bis: LocalDate): String {
        val buchungen = repository.buchungenFuerZeitraum(von, bis)
        val content = buildDatevContent(buchungen, von, bis)
        val fileName = "EXTF_Buchungsstapel_Kasse_${Formatter.datumDatevYearMonth(von)}.csv"
        return fileWriter.writeToExportFile(fileName, content)
    }

    // ====================================================
    // Einfaches Kassenbuch CSV
    // ====================================================

    /**
     * Erzeugt ein einfaches Kassenbuch-CSV und gibt den
     * Dateipfad zurueck.
     */
    suspend fun exportSimpleCsv(von: LocalDate, bis: LocalDate): String {
        val buchungen = repository.buchungenFuerZeitraum(von, bis)
        val vortrag = repository.vortragFuerTag(von)
        val content = buildSimpleContent(buchungen, von, vortrag)
        val fileName = "Kassenbuch_${Formatter.datumDatevYearMonth(von)}.csv"
        return fileWriter.writeToExportFile(fileName, content)
    }

    // ====================================================
    // DATEV content builder
    // ====================================================

    private fun buildDatevContent(
        buchungen: List<Buchung>,
        von: LocalDate,
        bis: LocalDate
    ): String {
        val sb = StringBuilder()

        // BOM fuer Excel
        sb.append('\uFEFF')

        // Header Zeile 1
        sb.append(buildDatevHeader1(von, bis))
        sb.append("\r\n")

        // Header Zeile 2 (Spaltennamen)
        sb.append(DATEV_COLUMN_NAMES)
        sb.append("\r\n")

        // Datenzeilen
        buchungen.forEach { buchung ->
            sb.append(buchungToDatevRow(buchung))
            sb.append("\r\n")
        }

        return sb.toString()
    }

    private fun buildDatevHeader1(von: LocalDate, bis: LocalDate): String {
        val wjBeginn = "${von.year}0101"
        val datumVon = Formatter.datumDatevFull(von)
        val datumBis = Formatter.datumDatevFull(bis)

        return listOf(
            "\"EXTF\"",                         // 1: Kennzeichen
            "700",                               // 2: Versionsnummer
            "21",                                // 3: Datenkategorie (Buchungsstapel)
            "\"Buchungsstapel\"",                // 4: Formatname
            "12",                                // 5: Formatversion
            "",                                  // 6: Erzeugt am
            "",                                  // 7: Importiert am
            "\"KB\"",                            // 8: Herkunft
            "\"${prefs.betriebsName}\"",         // 9: Exportiert von
            "\"\"",                              // 10: Importiert von
            prefs.beraterNr,                     // 11: Berater-Nr
            prefs.mandantNr,                     // 12: Mandanten-Nr
            wjBeginn,                            // 13: WJ-Beginn
            prefs.sachkontoLaenge.toString(),     // 14: Sachkontenlaenge
            datumVon,                            // 15: Datum von
            datumBis,                            // 16: Datum bis
            "\"Kassenbuch\"",                    // 17: Bezeichnung
            "\"\"",                              // 18: Diktatkuerzel
            "1",                                 // 19: Buchungstyp (1=Fibu)
            "0",                                 // 20: Rechnungslegungszweck
            "0",                                 // 21: Festschreibung
            "\"EUR\"",                           // 22: WKZ
            "", "", "", ""                       // 23-26: Reserved
        ).joinToString(";")
    }

    private fun buchungToDatevRow(buchung: Buchung): String {
        val row = Array(120) { "" }

        row[0] = Formatter.formatDecimalDatev(buchung.betrag)               // 1: Umsatz
        row[1] = "S"                                                         // 2: Soll/Haben
        row[2] = "EUR"                                                       // 3: WKZ
        // 4-5: leer
        row[6] = if (buchung.istEinnahme) "1000" else buchung.gegenkonto.toString()  // 7: Konto
        row[7] = if (buchung.istEinnahme) buchung.gegenkonto.toString() else "1000"  // 8: Gegenkonto
        row[8] = buchung.buSchluessel.toString()                             // 9: BU-Schluessel
        row[9] = Formatter.datumDatev(buchung.datum)                         // 10: Belegdatum (DDMM)
        row[10] = "\"${buchung.belegNr}\""                                   // 11: Belegfeld 1
        // 12: leer
        row[13] = "\"${buchung.buchungstext.take(60).replace("\"", "\"\"")}\""  // 14: Buchungstext

        return row.joinToString(";")
    }

    // ====================================================
    // Simple CSV content builder
    // ====================================================

    private fun buildSimpleContent(
        buchungen: List<Buchung>,
        von: LocalDate,
        vortrag: Double
    ): String {
        val sb = StringBuilder()

        // BOM fuer Excel
        sb.append('\uFEFF')

        // Header
        sb.append("Nr;Datum;Uhrzeit;Beleg-Nr;Buchungstext;Buchungsart;Gegenkonto;BU-Schluessel;Steuersatz;Einnahme;Ausgabe;Saldo\r\n")

        var saldo = vortrag
        var nr = 0

        buchungen.forEach { b ->
            nr++
            val einnahme = if (b.istEinnahme) b.betrag else 0.0
            val ausgabe = if (!b.istEinnahme) b.betrag else 0.0
            saldo += einnahme - ausgabe

            sb.append(
                listOf(
                    nr,
                    Formatter.datum(b.datum),
                    Formatter.uhrzeit(b.uhrzeit),
                    "\"${b.belegNr}\"",
                    "\"${b.buchungstext.replace("\"", "\"\"")}\"",
                    "\"${b.buchungsart.labelKey}\"",
                    b.gegenkonto,
                    b.buSchluessel,
                    "${b.steuersatz}%",
                    Formatter.formatDecimalDatev(einnahme),
                    Formatter.formatDecimalDatev(ausgabe),
                    Formatter.formatDecimalDatev(saldo)
                ).joinToString(";")
            )
            sb.append("\r\n")
        }

        return sb.toString()
    }

    // ====================================================
    // Constants
    // ====================================================

    companion object {
        /**
         * DATEV Buchungsstapel Spaltennamen (120 Spalten).
         * Nur die wichtigsten sind hier aufgefuehrt, der Rest bleibt leer.
         */
        val DATEV_COLUMN_NAMES: String = listOf(
            "Umsatz (ohne Soll/Haben-Kz)",
            "Soll/Haben-Kennzeichen",
            "WKZ Umsatz",
            "Kurs",
            "Basis-Umsatz",
            "WKZ Basis-Umsatz",
            "Konto",
            "Gegenkonto (ohne BU-Schluessel)",
            "BU-Schluessel",
            "Belegdatum",
            "Belegfeld 1",
            "Belegfeld 2",
            "Skonto",
            "Buchungstext"
        ).joinToString(";") + ";" + (15..120).joinToString(";") { "" }
    }
}
