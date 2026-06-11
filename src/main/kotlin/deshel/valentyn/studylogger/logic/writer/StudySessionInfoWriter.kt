package deshel.valentyn.studylogger.logic.writer

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object StudySessionInfoWriter {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun writeSessionInfo(project: Project) {
        try {
            val projectBasePath = project.basePath ?: return
            val sessionInfoFile = StudyLogPaths.sessionInfo(project) ?: return

            val appInfo = ApplicationInfo.getInstance()
            val startedAt = OffsetDateTime.now().format(formatter)
            val json = """
                {
                  "session_started_at": "$startedAt",
                  "project_name": "${escapeJson(project.name)}",
                  "project_path": "${escapeJson(projectBasePath)}",
                  "ide_name": "${escapeJson(appInfo.fullApplicationName)}",
                  "ide_build": "${escapeJson(appInfo.build.asString())}",
                  "plugin_id": "deshel.valentyn.studylogger",
                  "plugin_version": "1.0.0",
                  "os_name": "${escapeJson(System.getProperty("os.name"))}",
                  "os_version": "${escapeJson(System.getProperty("os.version"))}",
                  "os_arch": "${escapeJson(System.getProperty("os.arch"))}",
                  "java_version": "${escapeJson(System.getProperty("java.version"))}",
                  "timezone": "${escapeJson(ZoneId.systemDefault().id)}"
                }
            """.trimIndent()

            Files.writeString(
                sessionInfoFile,
                json,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        } catch (_: Exception) {
            // log
        }
    }
    //TODO refactor
     fun escapeJson(value: String?): String {
        if (value == null) return ""
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }
}