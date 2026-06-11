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

     fun logFileEvent(project: Project, eventType: String, file: VirtualFile) {
         if (!StudyLoggerState.isEnabled()) {
             return
         }
        try {
            val projectBasePath = project.basePath ?: return

            val timestamp = OffsetDateTime.now().format(formatter)
            val relativePath = StudyLogPaths.makeRelativePath(projectBasePath, file.path)
            val size = file.length
            val sha256 = StudyHashUtils.calculateSha256(file)
            val eventDataPrefix = "$timestamp | $eventType | $relativePath | size=$size | sha256=$sha256"
            appendChainedEvent(project, eventDataPrefix)
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
            val timestamp = OffsetDateTime.now().format(formatter)
            val projectName = project.name

            val eventDataPrefix = "$timestamp | $eventType | project=$projectName"
            appendChainedEvent(project, eventDataPrefix)
        } catch (_: Exception) {
            // log
        }
    }
    private fun appendChainedEvent(project: Project, eventDataPrefix: String) {
        val logFile = StudyLogPaths.fileEventsLog(project) ?: return

        val previousEventHash = getPreviousEventHash(logFile)
        val eventData = "$eventDataPrefix | prev_hash=$previousEventHash"
        val eventHash = StudyHashUtils.calculateTextSha256(eventData)
        val line = "$eventData | event_hash=$eventHash${System.lineSeparator()}"

        Files.writeString(
            logFile,
            line,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        )
    }
    fun logPluginStateEvent(project: Project, eventType: String) {
        try {
            val timestamp = OffsetDateTime.now().format(formatter)

            val eventDataPrefix = "$timestamp | $eventType | logger_enabled=${eventType == "PLUGIN_ENABLED"}"
            appendChainedEvent(project, eventDataPrefix)
        } catch (_: Exception) {
            // log
        }
    }
}