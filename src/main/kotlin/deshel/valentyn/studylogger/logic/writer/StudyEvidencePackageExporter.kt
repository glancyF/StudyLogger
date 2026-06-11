package deshel.valentyn.studylogger.logic.writer

import com.intellij.openapi.project.Project
import deshel.valentyn.studylogger.logic.dto.path.StudyLogPaths
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object StudyEvidencePackageExporter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")

    fun exportEvidencePackage(project: Project): Path? {
        return try {
            val logDirectory = StudyLogPaths.ensureLogDirectory(project) ?: return null

            val timestamp = OffsetDateTime.now().format(formatter)
            val zipFile = logDirectory.resolve("study-evidence-$timestamp.zip")

            val filesToExport = listOf(
                logDirectory.resolve("file-events.log"),
                logDirectory.resolve("verification-report.txt"),
                logDirectory.resolve("session-info.json"),
                logDirectory.resolve("session-final.json"),
                logDirectory.resolve("submission-evidence.txt")
            )

            val readmeFile = logDirectory.resolve("README-evidence.txt")
            writeEvidenceReadme(project, readmeFile)

            ZipOutputStream(
                Files.newOutputStream(
                    zipFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            ).use { zip ->
                for (file in filesToExport) {
                    addFileIfExists(zip, file, file.fileName.toString())
                }

                addFileIfExists(zip, readmeFile, readmeFile.fileName.toString())
            }

            zipFile
        } catch (_: Exception) {
            null
        }
    }

    private fun addFileIfExists(zip: ZipOutputStream, file: Path, entryName: String) {
        if (!Files.exists(file)) {
            return
        }

        zip.putNextEntry(ZipEntry(entryName))

        Files.newInputStream(file).use { input ->
            input.copyTo(zip)
        }

        zip.closeEntry()
    }

    private fun writeEvidenceReadme(project: Project, readmeFile: Path) {
        val text = """
            Study Integrity Logger — Evidence Package
            
            Project: ${project.name}
            Project path: ${project.basePath ?: "UNKNOWN"}
            
            This archive contains integrity evidence files only.
            It does not contain source code.
            
            Files:
            - file-events.log: chronological hash-chained event log
            - verification-report.txt: hash-chain verification result
            - session-info.json: IDE, OS, project and session metadata
            - session-final.json: final project/session integrity summary
            - submission-evidence.txt: selected submission file hash evidence
            
            Notes:
            - A VALID verification report means the event hash chain is internally consistent.
            - This package does not mathematically prove absence of all possible external sharing.
            - It provides a tamper-evident record of the observed IDE/project workflow.
        """.trimIndent()

        Files.writeString(
            readmeFile,
            text,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
}