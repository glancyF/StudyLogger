package deshel.valentyn.studylogger.logic

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object StudyPathFilter {
    //TODO can be refactor in somewhere else
     val ignoredExtensions = arrayListOf(
        ".class",
        ".jar",
        ".zip",
        ".log",
        ".iml",
        ".o",
        ".obj",
        ".exe",
        ".dylib",
        ".so",
        ".a"
    )
     val ignoredPathParts = arrayListOf(
        "/.git/",
        "/.idea/",
        "/.gradle/",
        "/target/",
        "/build/",
        "/out/",
        "/node_modules/",
        "/.study-log/",
        "/cmake-build-debug/",
        "/cmake-build-release/",
        "/CMakeFiles/"
    )
     val ignoredFileNames = listOf(
        "CMakeCache.txt",
        "compile_commands.json"
    )

    fun shouldLog(project: Project?, file: VirtualFile?): Boolean {
        if (project == null || file == null) {
            return false
        }
        if (file.isDirectory) {
            return false
        }
        if (!file.isInLocalFileSystem) {
            return false
        }
        val projectBasePath = project.basePath?.replace("\\", "/") ?: return false

        val filePath = file.path.replace("\\", "/")

        if (!filePath.startsWith(projectBasePath)) {
            return false
        }
        val relativePath = filePath.removePrefix(projectBasePath).removePrefix("/")
        if (relativePath == ".study-log" || relativePath.startsWith(".study-log/")) {
            return false
        }
        for (ignoredPart in ignoredPathParts) {
            if (filePath.contains(ignoredPart)) {
                return false
            }
        }
        val fileName = file.name
        if (fileName in ignoredFileNames) {
            return false
        }
        for (ignoredExtension in ignoredExtensions) {
            if (fileName.endsWith(ignoredExtension)) {
                return false
            }
        }
        return true
    }

}