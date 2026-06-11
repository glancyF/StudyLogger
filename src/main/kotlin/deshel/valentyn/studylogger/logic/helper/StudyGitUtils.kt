package deshel.valentyn.studylogger.logic.helper

import java.nio.file.Files
import java.nio.file.Path

object StudyGitUtils {
     fun readGitHeadCommit(projectRoot: Path): String {
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