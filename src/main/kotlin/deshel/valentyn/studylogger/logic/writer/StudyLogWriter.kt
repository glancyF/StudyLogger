package deshel.valentyn.studylogger.logic.writer

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import deshel.valentyn.studylogger.logic.helper.StudyHashUtils
import deshel.valentyn.studylogger.logic.state.StudyLoggerState
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object StudyLogWriter {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

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
         if (!StudyLoggerState.isEnabled()) {
             return
         }
        try {
            val projectBasePath = project.basePath ?: return
            val logFile = StudyLogPaths.fileEventsLog(project) ?: return

            val timestamp = OffsetDateTime.now().format(formatter)
            val relativePath = makeRelativePath(projectBasePath, file.path)
            val size = file.length
            val sha256 = StudyHashUtils.calculateSha256(file)
            val previousEventHash = getPreviousEventHash(logFile)
            val eventData = "$timestamp | $eventType | $relativePath | size=$size | sha256=$sha256 | prev_hash=$previousEventHash"
            val eventHash = StudyHashUtils.calculateTextSha256(eventData)
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

    fun logProjectEvent(project: Project, eventType: String) {
        if (!StudyLoggerState.isEnabled()) {
            return
        }
        try {
            val logFile = StudyLogPaths.fileEventsLog(project) ?: return

            val timestamp = OffsetDateTime.now().format(formatter)
            val projectName = project.name
            val previousEventHash = getPreviousEventHash(logFile)
            val eventData = "$timestamp | $eventType | project=$projectName | prev_hash=$previousEventHash"
            val eventHash = StudyHashUtils.calculateTextSha256(eventData)
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
    fun logPluginStateEvent(project: Project, eventType: String) {
        try {
            val logFile = StudyLogPaths.fileEventsLog(project) ?: return
            val timestamp = OffsetDateTime.now().format(formatter)

            val previousEventHash = getPreviousEventHash(logFile)

            val eventData = "$timestamp | $eventType | logger_enabled=${eventType == "PLUGIN_ENABLED"} | prev_hash=$previousEventHash"
            val eventHash = StudyHashUtils.calculateTextSha256(eventData)

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