package de.panda.kassenbuch.util

import de.panda.kassenbuch.platform.PlatformFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

object Formatter {

    fun euro(betrag: Double): String = PlatformFormatter.formatCurrency(betrag)

    fun dezimal(betrag: Double): String = PlatformFormatter.formatDecimal(betrag)

    fun datum(date: LocalDate): String {
        val d = date.dayOfMonth.toString().padStart(2, '0')
        val m = date.monthNumber.toString().padStart(2, '0')
        return "$d.$m.${date.year}"
    }

    fun datumKurz(date: LocalDate): String {
        val d = date.dayOfMonth.toString().padStart(2, '0')
        val m = date.monthNumber.toString().padStart(2, '0')
        return "$d.$m."
    }

    fun uhrzeit(time: LocalTime): String {
        val h = time.hour.toString().padStart(2, '0')
        val min = time.minute.toString().padStart(2, '0')
        return "$h:$min"
    }

    fun datumDatev(date: LocalDate): String {
        val d = date.dayOfMonth.toString().padStart(2, '0')
        val m = date.monthNumber.toString().padStart(2, '0')
        return "$d$m"
    }

    fun datumDatevFull(date: LocalDate): String {
        val d = date.dayOfMonth.toString().padStart(2, '0')
        val m = date.monthNumber.toString().padStart(2, '0')
        return "${date.year}$m$d"
    }

    fun datumDatevYearMonth(date: LocalDate): String {
        val m = date.monthNumber.toString().padStart(2, '0')
        return "${date.year}$m"
    }

    fun formatDecimalDatev(value: Double): String {
        val intPart = value.toLong()
        val fracPart = ((value - intPart) * 100 + 0.5).toLong()
        return "$intPart,${fracPart.toString().padStart(2, '0')}"
    }

    fun kontoLabel(konto: Int): String = when (konto) {
        1000 -> "1000 \u2014 Kasse"
        1200 -> "1200 \u2014 Bank"
        1360 -> "1360 \u2014 Geldtransit"
        1590 -> "1590 \u2014 Durchl. Posten"
        1800 -> "1800 \u2014 Privatentnahme"
        1890 -> "1890 \u2014 Privateinlage"
        3300 -> "3300 \u2014 Wareneingang 7%"
        3400 -> "3400 \u2014 Wareneingang 19%"
        4930 -> "4930 \u2014 B\u00fcrobedarf"
        4980 -> "4980 \u2014 Sonstige Kosten"
        8100 -> "8100 \u2014 Erl\u00f6se 7%"
        8300 -> "8300 \u2014 Erl\u00f6se 19%"
        else -> konto.toString()
    }

    fun steuersatzLabel(satz: Int): String = when (satz) {
        0 -> "0% (steuerfrei)"
        7 -> "7% USt"
        19 -> "19% USt"
        else -> "$satz%"
    }
}
