package deshel.valentyn.studylogger.logic.listener

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import deshel.valentyn.studylogger.logic.writer.StudyLogWriter
import deshel.valentyn.studylogger.logic.StudyPathFilter

class StudyFileEditorListener: FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val project = source.project
        if (!StudyPathFilter.shouldLog(project, file)) {
            return
        }
        StudyLogWriter.logFileEvent(project, "FILE_OPENED", file)
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val project = event.manager.project
        val file = event.newFile ?: return
        if(!StudyPathFilter.shouldLog(project, file)) {
            return
        }
        StudyLogWriter.logFileEvent(project,"FILE_SELECTED", file)
    }
}