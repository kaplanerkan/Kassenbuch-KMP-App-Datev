package de.panda.kassenbuch.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import de.panda.kassenbuch.data.db.KassenbuchDatabase
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSLocale

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KassenbuchDatabase.Schema, "kassenbuch.db")
    }
}

actual object PlatformFormatter {
    actual fun formatCurrency(amount: Double): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
            locale = NSLocale("de_DE")
        }
        return formatter.stringFromNumber(NSNumber(amount)) ?: "${amount} €"
    }

    actual fun formatDecimal(amount: Double): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = NSLocale("de_DE")
            minimumFractionDigits = 2u
            maximumFractionDigits = 2u
        }
        return formatter.stringFromNumber(NSNumber(amount)) ?: amount.toString()
    }
}

actual fun platformName(): String = "iOS"
