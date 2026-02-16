package de.panda.kassenbuch.util

import com.russhwolf.settings.Settings

/**
 * Einstellungen-Manager -- speichert Betriebsdaten und App-Einstellungen.
 *
 * Verwendet multiplatform-settings statt Android SharedPreferences,
 * damit die Einstellungen auf allen Plattformen funktionieren.
 */
class PrefsManager(private val settings: Settings) {

    // ── Betriebsdaten ──

    var betriebsName: String
        get() = settings.getString("betriebsname", "Mein Betrieb")
        set(value) = settings.putString("betriebsname", value)

    var adresse: String
        get() = settings.getString("adresse", "")
        set(value) = settings.putString("adresse", value)

    var steuerNummer: String
        get() = settings.getString("steuernummer", "")
        set(value) = settings.putString("steuernummer", value)

    // ── DATEV ──

    var beraterNr: String
        get() = settings.getString("berater_nr", "99999")
        set(value) = settings.putString("berater_nr", value)

    var mandantNr: String
        get() = settings.getString("mandant_nr", "10001")
        set(value) = settings.putString("mandant_nr", value)

    var sachkontoLaenge: Int
        get() = settings.getInt("sachkonto_laenge", 4)
        set(value) = settings.putInt("sachkonto_laenge", value)

    // ── Kassenbuch ──

    var standardWechselgeld: Double
        get() = settings.getString("standard_wechselgeld", "100.0").toDoubleOrNull() ?: 100.0
        set(value) = settings.putString("standard_wechselgeld", value.toString())

    // ── Theme ──

    var themeMode: String
        get() = settings.getString("theme_mode", "system")
        set(value) = settings.putString("theme_mode", value)
}
