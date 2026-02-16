package de.panda.kassenbuch.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.panda.kassenbuch.data.db.KassenbuchDatabase
import java.text.NumberFormat
import java.util.Locale

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbPath = System.getProperty("user.home") +
            java.io.File.separator + ".kassenbuch" +
            java.io.File.separator + "kassenbuch.db"
        val dbDir = java.io.File(dbPath).parentFile
        if (!dbDir.exists()) dbDir.mkdirs()

        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
        KassenbuchDatabase.Schema.create(driver)
        return driver
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

actual fun platformName(): String = "Desktop"
