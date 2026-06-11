package deshel.valentyn.studylogger.logic

import deshel.valentyn.studylogger.logic.helper.StudyHashUtils
import deshel.valentyn.studylogger.logic.writer.StudyLogWriter
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

object StudyProjectTreeHasher {

    fun calculateProjectTreeHash(projectRoot: Path): String {
        return try {
            if (!Files.exists(projectRoot)) {
                return "PROJECT_ROOT_NOT_FOUND"
            }
            val fileHashLines = Files.walk(projectRoot).use { stream ->
                stream.asSequence()
                    .filter { it.isRegularFile() }
                    .filter { shouldInclude(projectRoot, it) }
                    .map { path ->
                        val relativePath = normalizeRelativePath(projectRoot, path)
                        val fileHash = StudyHashUtils.calculatePathFileSha256(path)
                        "$relativePath=$fileHash"
                    }
                    .sorted()
                    .toList()
            }

            val treeData = fileHashLines.joinToString("\n")
            StudyHashUtils.calculateTextSha256(treeData)
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }

    private fun shouldInclude(projectRoot: Path, file: Path): Boolean {
        val relativePath = normalizeRelativePath(projectRoot, file)
        val normalized = "/$relativePath"
        for (ignoredPart in StudyPathFilter.ignoredPathParts) {
            if (normalized.contains(ignoredPart)) {
                return false
            }
        }
        if (relativePath == ".study-log" || relativePath.startsWith(".study-log/")) {
            return false
        }
        val fileName = file.fileName.toString()
        if (fileName in StudyPathFilter.ignoredFileNames) {
            return false
        }
        for (extension in StudyPathFilter.ignoredExtensions) {
            if (fileName.endsWith(extension)) {
                return false
            }
        }
        return true
    }

    private fun normalizeRelativePath(projectRoot: Path, file: Path): String {
        return projectRoot
            .relativize(file)
            .toString()
            .replace("\\", "/")
    }
}