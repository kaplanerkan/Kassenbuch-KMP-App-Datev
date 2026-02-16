package de.panda.kassenbuch.platform

import platform.Foundation.*

actual class PlatformFileWriter {
    actual suspend fun writeToExportFile(fileName: String, content: String): String {
        val fileManager = NSFileManager.defaultManager
        val documentsUrl = fileManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ).firstOrNull() as? NSURL ?: throw Exception("Documents directory not found")

        val kassenbuchDir = documentsUrl.URLByAppendingPathComponent("Kassenbuch")!!
        val dirPath = kassenbuchDir.path!!

        if (!fileManager.fileExistsAtPath(dirPath)) {
            fileManager.createDirectoryAtURL(
                kassenbuchDir,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
        }

        val fileUrl = kassenbuchDir.URLByAppendingPathComponent(fileName)!!
        val filePath = fileUrl.path!!

        val nsString = content as NSString
        nsString.writeToFile(filePath, atomically = true, encoding = NSUTF8StringEncoding, error = null)

        return filePath
    }
}
