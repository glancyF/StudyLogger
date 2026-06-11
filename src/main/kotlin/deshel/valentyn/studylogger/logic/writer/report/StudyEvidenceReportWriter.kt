package deshel.valentyn.studylogger.logic.writer.report

import com.intellij.openapi.project.Project
import deshel.valentyn.studylogger.logic.StudyLogVerifier
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object StudyEvidenceReportWriter {

    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun writeVerificationReport(project: Project) {
        try {
            val logFile = StudyLogPaths.fileEventsLog(project) ?: return
            val reportFile = StudyLogPaths.verificationReport(project) ?: return

            val result = StudyLogVerifier.verify(logFile)

            val report = buildString {
                appendLine("Study Integrity Log Verification")
                appendLine("Generated at: ${OffsetDateTime.now().format(formatter)}")
                appendLine()
                appendLine("Result: ${if (result.isValid) "VALID" else "BROKEN"}")
                appendLine("Checked events: ${result.checkedEvents}")
                appendLine("Final hash: ${result.finalHash}")
                appendLine("Message: ${result.message}")
            }

            Files.writeString(
                reportFile,
                report,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        } catch (_: Exception) {
            //TODO log
        }
    }
}