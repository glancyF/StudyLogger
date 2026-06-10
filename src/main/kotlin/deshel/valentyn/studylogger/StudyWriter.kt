package deshel.valentyn.studylogger

import com.intellij.openapi.vfs.VirtualFile
import java.time.format.DateTimeFormatter
import com.intellij.openapi.project.Project
import kotlinx.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.time.OffsetDateTime

object StudyWriter {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private const val maxHashSizeBytes: Long = 10L * 1024L * 1024L

    private fun calculateSha256(file: VirtualFile): String {
        return try {
            if (file.length > maxHashSizeBytes) {
                return "SKIPPED_TOO_LARGE" //TODO refactor into ENUM
            }
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream.use { input ->
                val buffer = ByteArray(8192)
                while (true) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) break
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "UNAVAILABLE"//TODO refactor it
        }
    }
    private fun makeRelativePath(projectBasePath: String, filePath: String): String {
        val normalizedBase = projectBasePath.replace("\\", "/")
        val normalizedFile = filePath.replace("\\", "/")
        return if (normalizedFile.startsWith(normalizedBase)) {
            normalizedFile
                .substring(normalizedBase.length)
                .replaceFirst("^/".toRegex(), "")
        } else {
            normalizedFile
        }
    }
     fun logFileEvent(project: Project, eventType: String, file: VirtualFile) {
        try {
            val projectBasePath = project.basePath ?: return
            val projectRoot = Path.of(projectBasePath)
            val logDirectory = projectRoot.resolve(".study-log")
            val logFile = logDirectory.resolve("file-events.log")
            Files.createDirectories(logFile.parent)

            val timestamp = OffsetDateTime.now().format(formatter)
            val relativePath = makeRelativePath(projectBasePath, file.path)
            val size = file.length
            val sha256 = calculateSha256(file)
            val line = "$timestamp | $eventType | $relativePath | size=$size | sha256=$sha256${System.lineSeparator()}"

            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } catch (e: IOException) {
            //TODO logging here
        }
    }
}