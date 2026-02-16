package de.panda.kassenbuch.platform

import java.io.File
import java.io.FileWriter

actual class PlatformFileWriter {
    actual suspend fun writeToExportFile(fileName: String, content: String): String {
        val dir = File(
            System.getProperty("user.home"),
            "Documents${File.separator}Kassenbuch"
        )
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)

        FileWriter(file, Charsets.UTF_8).use { writer ->
            writer.write(content)
        }

        return file.absolutePath
    }
}
