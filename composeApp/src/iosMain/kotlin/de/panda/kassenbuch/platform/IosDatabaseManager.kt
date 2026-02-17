package de.panda.kassenbuch.platform

actual class PlatformDatabaseManager {

    actual fun getDatabasePath(): String = "kassenbuch.db"

    actual fun getBackupDir(): String = ""

    actual suspend fun createBackup(): String {
        throw UnsupportedOperationException("iOS Backup noch nicht implementiert")
    }

    actual suspend fun restoreFromBackup(zipPath: String) {
        throw UnsupportedOperationException("iOS Restore noch nicht implementiert")
    }

    actual fun listBackups(): List<String> = emptyList()
}
