package deshel.valentyn.studylogger.logic.helper

object StudyJsonUtils {

    fun escapeJson(value: String?): String {
        if (value == null) return ""

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }
}