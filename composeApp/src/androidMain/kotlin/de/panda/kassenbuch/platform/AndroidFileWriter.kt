package de.panda.kassenbuch.platform

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

actual class PlatformFileWriter(private val context: Context) {
    actual suspend fun writeToExportFile(fileName: String, content: String): String {
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Kassenbuch"
        )
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)

        FileWriter(file, Charsets.UTF_8).use { writer ->
            writer.write(content)
        }

        return file.absolutePath
    }
}
