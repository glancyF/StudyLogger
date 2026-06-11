package deshel.valentyn.studylogger.writer

import com.intellij.openapi.project.Project
import deshel.valentyn.studylogger.logic.StudyLogVerifier
import deshel.valentyn.studylogger.logic.StudyProjectTreeHasher
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import deshel.valentyn.studylogger.logic.helper.StudyJsonUtils
import deshel.valentyn.studylogger.logic.writer.StudySessionInfoWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object StudyFinalReportWriter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun writeFinalReport(project: Project) {
        try {
            val projectBasePath = project.basePath ?: return
            val projectRoot = StudyLogPaths.projectRoot(project) ?: return
            val logFile = StudyLogPaths.fileEventsLog(project) ?: return
            val finalReportFile = StudyLogPaths.sessionFinal(project) ?: return

            val verificationResult = StudyLogVerifier.verify(logFile)
            val projectTreeHash = StudyProjectTreeHasher.calculateProjectTreeHash(projectRoot)
            val gitHeadCommit = readGitHeadCommit(projectRoot)

            val json = """
                {
                  "generated_at": "${OffsetDateTime.now().format(formatter)}",
                  "project_name": "${StudyJsonUtils.escapeJson(project.name)}",
                  "project_path": "${StudyJsonUtils.escapeJson(projectBasePath)}",
                  "verification_result": "${if (verificationResult.isValid) "VALID" else "BROKEN"}",
                  "checked_events": ${verificationResult.checkedEvents},
                  "final_log_hash": "${StudyJsonUtils.escapeJson(verificationResult.finalHash)}",
                  "verification_message": "${StudyJsonUtils.escapeJson(verificationResult.message)}",
                  "project_tree_hash": "${StudyJsonUtils.escapeJson(projectTreeHash)}",
                  "git_head_commit": "${StudyJsonUtils.escapeJson(gitHeadCommit)}"
                }
            """.trimIndent()

            Files.writeString(
                finalReportFile,
                json,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        } catch (_: Exception) {
            // Final report generation must never break the IDE.
        }
    }

    private fun readGitHeadCommit(projectRoot: Path): String {
        return try {
            val headFile = projectRoot.resolve(".git").resolve("HEAD")

            if (!Files.exists(headFile)) {
                return "NO_GIT_REPOSITORY"
            }

            val headContent = Files.readString(headFile).trim()

            if (headContent.startsWith("ref:")) {
                val refPath = headContent.removePrefix("ref:").trim()
                val refFile = projectRoot.resolve(".git").resolve(refPath)

                if (Files.exists(refFile)) {
                    Files.readString(refFile).trim()
                } else {
                    "GIT_REF_NOT_FOUND"
                }
            } else {
                headContent
            }
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }
}