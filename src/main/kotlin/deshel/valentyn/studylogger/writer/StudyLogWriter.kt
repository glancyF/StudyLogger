package deshel.valentyn.studylogger.writer

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jsonProtocol.EventType
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object StudyLogWriter {
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
            val previousEventHash = getPreviousEventHash(logFile)
            val eventData = "$timestamp | $eventType | $relativePath | size=$size | sha256=$sha256 | prev_hash=$previousEventHash"
            val eventHash = calculateTextSha256(eventData)
            val line = "$eventData | event_hash=$eventHash${System.lineSeparator()}"
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        } catch (_: Exception) {
            //TODO logging here
        }
    }
    private fun getPreviousEventHash(logFile: Path): String {
        return try {
            if (!Files.exists(logFile)) {
                return "GENESIS"
            }
            val lines = Files.readAllLines(logFile)

            if (lines.isEmpty()) {
                return "GENESIS"
            }
            val lastLine = lines.lastOrNull { it.contains("event_hash=") } ?: return "GENESIS"
            val marker = "event_hash="
            lastLine.substringAfter(marker).trim()
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }
    private fun calculateTextSha256(text: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(text.toByteArray(StandardCharsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }

    fun logProjectEvent(project: Project, eventType: String) {
        try {
            val projectBasePath = project.basePath ?: return

            val projectRoot = Path.of(projectBasePath)
            val logDirectory = projectRoot.resolve(".study-log")
            val logFile = logDirectory.resolve("file-events.log")

            Files.createDirectories(logDirectory)

            val timestamp = OffsetDateTime.now().format(formatter)
            val projectName = project.name
            val previousEventHash = getPreviousEventHash(logFile)
            val eventData = "$timestamp | $eventType | project=$projectName | prev_hash=$previousEventHash"
            val eventHash = calculateTextSha256(eventData)
            val line = "$eventData | event_hash=$eventHash${System.lineSeparator()}"

            Files.writeString(
                logFile,
                line,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            )
        } catch (_: Exception) {
            // log
        }
    }
}