package de.panda.kassenbuch.util

import de.panda.kassenbuch.data.entity.Buchung
import de.panda.kassenbuch.data.entity.BuchungsArt
import de.panda.kassenbuch.data.repository.KassenbuchRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Befuellt die Datenbank mit einer Woche realistischer Demo-Daten
 * fuer einen Doener-Imbiss, wenn die Datenbank leer ist.
 */
suspend fun seedDemoDataIfEmpty(repository: KassenbuchRepository) {
    if (repository.gesamtAnzahl() > 0) return

    val heute = currentDate()
    // 7 Tage zurueck + heute = 8 Tage
    val startTag = heute.minusDays(7)

    var belegCounter = 1

    for (dayOffset in 0..7) {
        val tag = startTag.minusDays(-dayOffset) // +dayOffset
        val istSonntag = dayOffset == 6

        // Morgens: Wechselgeld-Einlage (100 EUR)
        repository.buchungSpeichern(
            Buchung(
                datum = tag,
                uhrzeit = LocalTime(7, 30),
                buchungsart = BuchungsArt.WECHSELGELD_EINLAGE,
                betrag = 100.0,
                buchungstext = "Wechselgeld Einlage",
                gegenkonto = BuchungsArt.WECHSELGELD_EINLAGE.defaultGegenkonto,
                buSchluessel = BuchungsArt.WECHSELGELD_EINLAGE.defaultBuSchluessel,
                steuersatz = BuchungsArt.WECHSELGELD_EINLAGE.defaultSteuersatz,
                belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                istEinnahme = true
            )
        )

        if (istSonntag) {
            // Sonntag: Ruhetag — nur Wechselgeld rein/raus
            repository.buchungSpeichern(
                Buchung(
                    datum = tag,
                    uhrzeit = LocalTime(8, 0),
                    buchungsart = BuchungsArt.WECHSELGELD_ENTNAHME,
                    betrag = 100.0,
                    buchungstext = "Wechselgeld Entnahme (Ruhetag)",
                    gegenkonto = BuchungsArt.WECHSELGELD_ENTNAHME.defaultGegenkonto,
                    buSchluessel = BuchungsArt.WECHSELGELD_ENTNAHME.defaultBuSchluessel,
                    steuersatz = BuchungsArt.WECHSELGELD_ENTNAHME.defaultSteuersatz,
                    belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                    istEinnahme = false
                )
            )
            continue
        }

        // Tagesumsatz Speisen 7% (Z-Bericht) — variiert pro Tag
        val speisen7 = when (dayOffset) {
            0 -> 485.30  // Mo
            1 -> 520.80  // Di
            2 -> 610.50  // Mi
            3 -> 545.20  // Do
            4 -> 780.90  // Fr
            5 -> 890.40  // Sa
            7 -> 430.60  // Heute
            else -> 0.0
        }
        repository.buchungSpeichern(
            Buchung(
                datum = tag,
                uhrzeit = LocalTime(21, 0),
                buchungsart = BuchungsArt.TAGESUMSATZ_7,
                betrag = speisen7,
                buchungstext = "Z-Bericht Speisen 7%",
                gegenkonto = BuchungsArt.TAGESUMSATZ_7.defaultGegenkonto,
                buSchluessel = BuchungsArt.TAGESUMSATZ_7.defaultBuSchluessel,
                steuersatz = BuchungsArt.TAGESUMSATZ_7.defaultSteuersatz,
                belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                istEinnahme = true
            )
        )

        // Tagesumsatz Getraenke 19% (Z-Bericht)
        val getraenke19 = when (dayOffset) {
            0 -> 125.60  // Mo
            1 -> 148.30  // Di
            2 -> 165.90  // Mi
            3 -> 138.70  // Do
            4 -> 245.50  // Fr
            5 -> 310.20  // Sa
            7 -> 98.40   // Heute
            else -> 0.0
        }
        repository.buchungSpeichern(
            Buchung(
                datum = tag,
                uhrzeit = LocalTime(21, 5),
                buchungsart = BuchungsArt.TAGESUMSATZ_19,
                betrag = getraenke19,
                buchungstext = "Z-Bericht Getraenke 19%",
                gegenkonto = BuchungsArt.TAGESUMSATZ_19.defaultGegenkonto,
                buSchluessel = BuchungsArt.TAGESUMSATZ_19.defaultBuSchluessel,
                steuersatz = BuchungsArt.TAGESUMSATZ_19.defaultSteuersatz,
                belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                istEinnahme = true
            )
        )

        // Wareneinkauf (nicht jeden Tag)
        when (dayOffset) {
            0 -> {
                // Montag: Grosseinkauf Lebensmittel 7%
                repository.buchungSpeichern(
                    Buchung(
                        datum = tag,
                        uhrzeit = LocalTime(9, 15),
                        buchungsart = BuchungsArt.WARENEINKAUF_7,
                        betrag = 285.40,
                        buchungstext = "Netto Einkauf Fleisch/Gemuese",
                        gegenkonto = BuchungsArt.WARENEINKAUF_7.defaultGegenkonto,
                        buSchluessel = BuchungsArt.WARENEINKAUF_7.defaultBuSchluessel,
                        steuersatz = BuchungsArt.WARENEINKAUF_7.defaultSteuersatz,
                        belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                        istEinnahme = false
                    )
                )
            }
            2 -> {
                // Mittwoch: Getraenke-Nachbestellung 19%
                repository.buchungSpeichern(
                    Buchung(
                        datum = tag,
                        uhrzeit = LocalTime(10, 0),
                        buchungsart = BuchungsArt.WARENEINKAUF_19,
                        betrag = 142.80,
                        buchungstext = "Getraenke-Grosshandel",
                        gegenkonto = BuchungsArt.WARENEINKAUF_19.defaultGegenkonto,
                        buSchluessel = BuchungsArt.WARENEINKAUF_19.defaultBuSchluessel,
                        steuersatz = BuchungsArt.WARENEINKAUF_19.defaultSteuersatz,
                        belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                        istEinnahme = false
                    )
                )
            }
            3 -> {
                // Donnerstag: Lebensmittel-Nachbestellung 7%
                repository.buchungSpeichern(
                    Buchung(
                        datum = tag,
                        uhrzeit = LocalTime(9, 30),
                        buchungsart = BuchungsArt.WARENEINKAUF_7,
                        betrag = 198.60,
                        buchungstext = "Metro Einkauf Zutaten",
                        gegenkonto = BuchungsArt.WARENEINKAUF_7.defaultGegenkonto,
                        buSchluessel = BuchungsArt.WARENEINKAUF_7.defaultBuSchluessel,
                        steuersatz = BuchungsArt.WARENEINKAUF_7.defaultSteuersatz,
                        belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                        istEinnahme = false
                    )
                )
            }
            4 -> {
                // Freitag: Buerokosten
                repository.buchungSpeichern(
                    Buchung(
                        datum = tag,
                        uhrzeit = LocalTime(11, 0),
                        buchungsart = BuchungsArt.BUEROKOSTEN,
                        betrag = 24.90,
                        buchungstext = "Kassenrollen + Stifte",
                        gegenkonto = BuchungsArt.BUEROKOSTEN.defaultGegenkonto,
                        buSchluessel = BuchungsArt.BUEROKOSTEN.defaultBuSchluessel,
                        steuersatz = BuchungsArt.BUEROKOSTEN.defaultSteuersatz,
                        belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                        istEinnahme = false
                    )
                )
            }
            5 -> {
                // Samstag: Sonstige Ausgabe (Reinigung)
                repository.buchungSpeichern(
                    Buchung(
                        datum = tag,
                        uhrzeit = LocalTime(8, 30),
                        buchungsart = BuchungsArt.SONSTIGE_AUSGABE,
                        betrag = 35.70,
                        buchungstext = "Reinigungsmittel",
                        gegenkonto = BuchungsArt.SONSTIGE_AUSGABE.defaultGegenkonto,
                        buSchluessel = BuchungsArt.SONSTIGE_AUSGABE.defaultBuSchluessel,
                        steuersatz = BuchungsArt.SONSTIGE_AUSGABE.defaultSteuersatz,
                        belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                        istEinnahme = false
                    )
                )
            }
            7 -> {
                // Heute: Wareneinkauf Lebensmittel
                repository.buchungSpeichern(
                    Buchung(
                        datum = tag,
                        uhrzeit = LocalTime(9, 0),
                        buchungsart = BuchungsArt.WARENEINKAUF_7,
                        betrag = 156.30,
                        buchungstext = "Einkauf Fleisch/Salat",
                        gegenkonto = BuchungsArt.WARENEINKAUF_7.defaultGegenkonto,
                        buSchluessel = BuchungsArt.WARENEINKAUF_7.defaultBuSchluessel,
                        steuersatz = BuchungsArt.WARENEINKAUF_7.defaultSteuersatz,
                        belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                        istEinnahme = false
                    )
                )
            }
        }

        // Abends: Wechselgeld-Entnahme (100 EUR)
        repository.buchungSpeichern(
            Buchung(
                datum = tag,
                uhrzeit = LocalTime(21, 30),
                buchungsart = BuchungsArt.WECHSELGELD_ENTNAHME,
                betrag = 100.0,
                buchungstext = "Wechselgeld Entnahme",
                gegenkonto = BuchungsArt.WECHSELGELD_ENTNAHME.defaultGegenkonto,
                buSchluessel = BuchungsArt.WECHSELGELD_ENTNAHME.defaultBuSchluessel,
                steuersatz = BuchungsArt.WECHSELGELD_ENTNAHME.defaultSteuersatz,
                belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                istEinnahme = false
            )
        )

        // Freitag: Bankeinzahlung (Wochenerloes)
        if (dayOffset == 4) {
            repository.buchungSpeichern(
                Buchung(
                    datum = tag,
                    uhrzeit = LocalTime(15, 0),
                    buchungsart = BuchungsArt.BANKEINZAHLUNG,
                    betrag = 1500.0,
                    buchungstext = "Bankeinzahlung Wochenerloes",
                    gegenkonto = BuchungsArt.BANKEINZAHLUNG.defaultGegenkonto,
                    buSchluessel = BuchungsArt.BANKEINZAHLUNG.defaultBuSchluessel,
                    steuersatz = BuchungsArt.BANKEINZAHLUNG.defaultSteuersatz,
                    belegNr = "B${tag.toString().replace("-", "")}-${belegCounter++}",
                    istEinnahme = false
                )
            )
        }
    }
}
