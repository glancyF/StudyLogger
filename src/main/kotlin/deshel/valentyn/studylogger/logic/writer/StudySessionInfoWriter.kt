package deshel.valentyn.studylogger.logic.writer

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import deshel.valentyn.studylogger.logic.helper.StudyJsonUtils
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
                  "project_name": "${StudyJsonUtils.escapeJson(project.name)}",
                  "project_path": "${StudyJsonUtils.escapeJson(projectBasePath)}",
                  "ide_name": "${StudyJsonUtils.escapeJson(appInfo.fullApplicationName)}",
                  "ide_build": "${StudyJsonUtils.escapeJson(appInfo.build.asString())}",
                  "plugin_id": "deshel.valentyn.studylogger",
                  "plugin_version": "1.0.0",
                  "os_name": "${StudyJsonUtils.escapeJson(System.getProperty("os.name"))}",
                  "os_version": "${StudyJsonUtils.escapeJson(System.getProperty("os.version"))}",
                  "os_arch": "${StudyJsonUtils.escapeJson(System.getProperty("os.arch"))}",
                  "java_version": "${StudyJsonUtils.escapeJson(System.getProperty("java.version"))}",
                  "timezone": "${StudyJsonUtils.escapeJson(ZoneId.systemDefault().id)}"
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
}