package de.panda.kassenbuch.platform

expect class PlatformDatabaseManager {
    fun getDatabasePath(): String
    fun getBackupDir(): String
    suspend fun createBackup(): String
    suspend fun restoreFromBackup(zipPath: String)
    fun listBackups(): List<String>
}
