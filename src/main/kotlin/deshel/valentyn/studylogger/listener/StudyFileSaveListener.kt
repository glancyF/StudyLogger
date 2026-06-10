package deshel.valentyn.studylogger.listener

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile
import deshel.valentyn.studylogger.writer.report.StudyEvidenceReportWriter
import deshel.valentyn.studylogger.writer.StudyLogWriter
import deshel.valentyn.studylogger.StudyPathFilter

class StudyFileSaveListener: FileDocumentManagerListener {
    override fun beforeDocumentSaving(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        val project = findProjectForFile(file) ?: return

        if(!StudyPathFilter.shouldLog(project,file)) {
            return
        }
        StudyLogWriter.logFileEvent(project, "FILE_SAVED", file)
//        StudyEvidenceReportWriter.writeVerificationReport(project); //TODO temp
    }

    private fun findProjectForFile(file: VirtualFile): Project? {
        val filePath = file.path.replace("\\", "/")

        for (project in ProjectManager.getInstance().openProjects) {
            val basePath = project.basePath?.replace("\\", "/") ?: continue

            if (filePath.startsWith(basePath)) {
                return project
            }
        }
        return null
    }

}