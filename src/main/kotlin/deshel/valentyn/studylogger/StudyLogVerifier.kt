package deshel.valentyn.studylogger

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

object StudyLogVerifier {

    fun verify(logFile: Path): VerificationResult {
        return try {
            if (!Files.exists(logFile)) {
                return VerificationResult(
                    isValid = false,
                    checkedEvents = 0,
                    finalHash = "NONE",
                    message = "Log file does not exist"
                )
            }

            val lines = Files.readAllLines(logFile)
                .filter { it.isNotBlank() }

            if (lines.isEmpty()) {
                return VerificationResult(
                    isValid = false,
                    checkedEvents = 0,
                    finalHash = "NONE",
                    message = "Log file is empty"
                )
            }

            var expectedPreviousHash = "GENESIS"
            var finalHash = "GENESIS"

            for ((index, line) in lines.withIndex()) {
                val previousHash = extractValue(line, "prev_hash=")
                val eventHash = extractValue(line, "event_hash=")

                if (previousHash == null) {
                    return VerificationResult(
                        isValid = false,
                        checkedEvents = index,
                        finalHash = finalHash,
                        message = "Broken at line ${index + 1}: missing prev_hash"
                    )
                }

                if (eventHash == null) {
                    return VerificationResult(
                        isValid = false,
                        checkedEvents = index,
                        finalHash = finalHash,
                        message = "Broken at line ${index + 1}: missing event_hash"
                    )
                }

                if (previousHash != expectedPreviousHash) {
                    return VerificationResult(
                        isValid = false,
                        checkedEvents = index,
                        finalHash = finalHash,
                        message = "Broken at line ${index + 1}: prev_hash mismatch"
                    )
                }

                val eventData = line.substringBefore(" | event_hash=").trim()
                val recalculatedHash = calculateTextSha256(eventData)

                if (eventHash != recalculatedHash) {
                    return VerificationResult(
                        isValid = false,
                        checkedEvents = index,
                        finalHash = finalHash,
                        message = "Broken at line ${index + 1}: event_hash mismatch"
                    )
                }

                expectedPreviousHash = eventHash
                finalHash = eventHash
            }

            VerificationResult(
                isValid = true,
                checkedEvents = lines.size,
                finalHash = finalHash,
                message = "VALID"
            )
        } catch (exception: Exception) {
            VerificationResult(
                isValid = false,
                checkedEvents = 0,
                finalHash = "UNAVAILABLE",
                message = "Verification failed: ${exception.message}"
            )
        }
    }

    private fun extractValue(line: String, marker: String): String? {
        if (!line.contains(marker)) {
            return null
        }

        return line.substringAfter(marker)
            .substringBefore(" | ")
            .trim()
            .ifBlank { null }
    }

    private fun calculateTextSha256(text: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(text.toByteArray(StandardCharsets.UTF_8))

            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            "UNAVAILABLE"
        }
    }
}

data class VerificationResult(
    val isValid: Boolean,
    val checkedEvents: Int,
    val finalHash: String,
    val message: String
)