package de.panda.kassenbuch.platform

expect class PlatformFileWriter {
    suspend fun writeToExportFile(fileName: String, content: String): String
}
