package de.panda.kassenbuch.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

actual class PlatformDatabaseManager {

    private fun dbFilePath(): String =
        System.getProperty("user.home") + File.separator + ".kassenbuch" + File.separator + "kassenbuch.db"

    private fun backupDirPath(): String =
        System.getProperty("user.home") + File.separator + ".kassenbuch" +
            File.separator + "logs" + File.separator + "kassenbuch" + File.separator + "backup"

    actual fun getDatabasePath(): String = dbFilePath()

    actual fun getBackupDir(): String = backupDirPath()

    actual suspend fun createBackup(): String = withContext(Dispatchers.IO) {
        val backupDirFile = File(backupDirPath())
        if (!backupDirFile.exists()) backupDirFile.mkdirs()

        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm"))
        val zipFileName = "kassenbuch_$timestamp.zip"
        val zipFile = File(backupDirFile, zipFileName)

        val dbFile = File(dbFilePath())
        if (!dbFile.exists()) throw FileNotFoundException("Datenbank nicht gefunden: ${dbFilePath()}")

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            zos.putNextEntry(ZipEntry("kassenbuch.db"))
            BufferedInputStream(FileInputStream(dbFile)).use { bis ->
                bis.copyTo(zos)
            }
            zos.closeEntry()
        }

        zipFile.absolutePath
    }

    actual suspend fun restoreFromBackup(zipPath: String): Unit = withContext(Dispatchers.IO) {
        val zipFile = File(zipPath)
        if (!zipFile.exists()) throw FileNotFoundException("Backup-Datei nicht gefunden: $zipPath")

        val db = dbFilePath()
        val dbFile = File(db)
        val tempFile = File("$db.tmp")

        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (entry.name == "kassenbuch.db") {
                    BufferedOutputStream(FileOutputStream(tempFile)).use { bos ->
                        zis.copyTo(bos)
                    }
                    break
                }
                entry = zis.nextEntry
            }
        }

        if (!tempFile.exists()) throw IllegalStateException("ZIP enthält keine kassenbuch.db")

        if (dbFile.exists()) dbFile.delete()
        tempFile.renameTo(dbFile)
    }

    actual fun listBackups(): List<String> {
        val dir = File(backupDirPath())
        if (!dir.exists()) return emptyList()
        return dir.listFiles { f -> f.extension == "zip" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { it.absolutePath }
            ?: emptyList()
    }
}
