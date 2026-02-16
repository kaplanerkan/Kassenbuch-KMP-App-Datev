package de.panda.kassenbuch.data.entity

enum class BuchungsArt(
    val labelKey: String,
    val defaultGegenkonto: Int,
    val defaultBuSchluessel: Int,
    val defaultSteuersatz: Int,
    val einnahme: Boolean
) {
    // Einnahmen
    TAGESUMSATZ_7("type_tagesumsatz_7", 8100, 2, 7, true),
    TAGESUMSATZ_19("type_tagesumsatz_19", 8300, 3, 19, true),
    GUTSCHEIN_VERKAUF("type_gutschein_verkauf", 1590, 40, 0, true),
    WECHSELGELD_EINLAGE("type_wechselgeld_ein", 1890, 40, 0, true),
    PRIVATEINLAGE("type_privateinlage", 1890, 40, 0, true),
    SONSTIGE_EINNAHME("type_sonstige_einnahme", 8100, 2, 7, true),

    // Ausgaben
    WARENEINKAUF_7("type_wareneinkauf_7", 3300, 8, 7, false),
    WARENEINKAUF_19("type_wareneinkauf_19", 3400, 9, 19, false),
    WECHSELGELD_ENTNAHME("type_wechselgeld_aus", 1800, 40, 0, false),
    PRIVATENTNAHME("type_privatentnahme", 1800, 40, 0, false),
    BANKEINZAHLUNG("type_bankeinzahlung", 1360, 40, 0, false),
    BUEROKOSTEN("type_buerokosten", 4930, 9, 19, false),
    SONSTIGE_AUSGABE("type_sonstige_ausgabe", 4980, 9, 19, false);

    companion object {
        fun einnahmeArten() = entries.filter { it.einnahme }
        fun ausgabeArten() = entries.filter { !it.einnahme }
    }
}
