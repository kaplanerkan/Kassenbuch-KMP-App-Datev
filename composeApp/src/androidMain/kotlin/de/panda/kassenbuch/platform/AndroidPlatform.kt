package de.panda.kassenbuch.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import de.panda.kassenbuch.data.db.KassenbuchDatabase
import java.text.NumberFormat
import java.util.Locale

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(KassenbuchDatabase.Schema, context, "kassenbuch.db")
    }
}

actual object PlatformFormatter {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    private val numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    actual fun formatCurrency(amount: Double): String = currencyFormat.format(amount)
    actual fun formatDecimal(amount: Double): String = numberFormat.format(amount)
}

actual fun platformName(): String = "Android"
