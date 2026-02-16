package de.panda.kassenbuch.platform

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

expect object PlatformFormatter {
    fun formatCurrency(amount: Double): String
    fun formatDecimal(amount: Double): String
}

expect fun platformName(): String
