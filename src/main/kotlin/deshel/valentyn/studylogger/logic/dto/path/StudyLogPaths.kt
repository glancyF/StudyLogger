package deshel.valentyn.studylogger.logic.dto.path

import com.intellij.openapi.project.Project
import java.nio.file.Files
import java.nio.file.Path

object StudyLogPaths {

    private const val STUDY_LOG_DIR = ".study-log"

    fun projectRoot(project: Project): Path? {
        val basePath = project.basePath ?: return null
        return Path.of(basePath)
    }

    fun logDirectory(project: Project): Path? {
        val root = projectRoot(project) ?: return null
        return root.resolve(STUDY_LOG_DIR)
    }

    fun ensureLogDirectory(project: Project): Path? {
        val directory = logDirectory(project) ?: return null
        Files.createDirectories(directory)
        return directory
    }

    fun fileEventsLog(project: Project): Path? {
        val directory = ensureLogDirectory(project) ?: return null
        return directory.resolve("file-events.log")
    }

    fun verificationReport(project: Project): Path? {
        val directory = ensureLogDirectory(project) ?: return null
        return directory.resolve("verification-report.txt")
    }

    fun sessionInfo(project: Project): Path? {
        val directory = ensureLogDirectory(project) ?: return null
        return directory.resolve("session-info.json")
    }

    fun sessionFinal(project: Project): Path? {
        val directory = ensureLogDirectory(project) ?: return null
        return directory.resolve("session-final.json")
    }
    // TODO Future
    fun submissionEvidence(project: Project): Path? {
        val directory = ensureLogDirectory(project) ?: return null
        return directory.resolve("submission-evidence.txt")
    }
}