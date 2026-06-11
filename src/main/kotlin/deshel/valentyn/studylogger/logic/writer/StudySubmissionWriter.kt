package deshel.valentyn.studylogger.logic.writer

import com.intellij.openapi.project.Project
import deshel.valentyn.studylogger.logic.StudyLogVerifier
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import deshel.valentyn.studylogger.logic.helper.StudyGitUtils
import deshel.valentyn.studylogger.logic.helper.StudyHashUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object StudySubmissionEvidenceWriter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun writeSubmissionEvidence(project: Project, submissionFile: Path) {
        try {
            val projectRoot = StudyLogPaths.projectRoot(project) ?: return
            val evidenceFile = StudyLogPaths.submissionEvidence(project) ?: return
            val logFile = StudyLogPaths.fileEventsLog(project) ?: return

            val generatedAt = OffsetDateTime.now().format(formatter)
            val submissionSha256 = StudyHashUtils.calculatePathFileSha256(submissionFile)
            val verificationResult = StudyLogVerifier.verify(logFile)
            val gitHeadCommit = StudyGitUtils.readGitHeadCommit(projectRoot)

            val text = buildString {
                appendLine("Study Submission Evidence")
                appendLine("Generated at: $generatedAt")
                appendLine()
                appendLine("Project: ${project.name}")
                appendLine("Project path: ${project.basePath ?: "UNKNOWN"}")
                appendLine()
                appendLine("Submission file: ${submissionFile.toAbsolutePath()}")
                appendLine("Submission SHA-256: $submissionSha256")
                appendLine()
                appendLine("Verification result: ${if (verificationResult.isValid) "VALID" else "BROKEN"}")
                appendLine("Checked events: ${verificationResult.checkedEvents}")
                appendLine("Final log hash: ${verificationResult.finalHash}")
                appendLine("Verification message: ${verificationResult.message}")
                appendLine()
                appendLine("Git HEAD: $gitHeadCommit")
            }

            Files.writeString(
                evidenceFile,
                text,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )

            StudyLogWriter.logSubmissionHashCreated(
                project = project,
                submissionFile = submissionFile,
                submissionSha256 = submissionSha256
            )
        } catch (_: Exception) {
            // Submission evidence generation must never break the IDE.
        }
    }
}