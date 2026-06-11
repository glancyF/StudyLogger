package deshel.valentyn.studylogger.logic.helper

import com.intellij.openapi.vfs.VirtualFile
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

object StudyHashUtils {
    private const val maxHashSizeBytes: Long = 10L * 1024L * 1024L

    fun calculateTextSha256(text: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(text.toByteArray(StandardCharsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }
    fun calculateSha256(file: VirtualFile): String {
        return try {
            if (file.length > maxHashSizeBytes) {
                return "SKIPPED_TOO_LARGE" //TODO refactor into ENUM
            }
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream.use { input ->
                val buffer = ByteArray(8192)
                while (true) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) break
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "UNAVAILABLE"//TODO refactor it
        }
    }
    fun calculatePathFileSha256(file: Path): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")

            Files.newInputStream(file).use { input ->
                val buffer = ByteArray(8192)

                while (true) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) break
                    digest.update(buffer, 0, bytesRead)
                }
            }

            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }
}