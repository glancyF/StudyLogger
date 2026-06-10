package deshel.valentyn.studylogger.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import deshel.valentyn.studylogger.writer.StudyLogWriter

class StudyProjectActivityListener : ProjectManagerListener {
    override fun projectOpened(project: Project) {
        StudyLogWriter.logProjectEvent(project, "PROJECT_OPENED")
    }

    override fun projectClosing(project: Project) {
        StudyLogWriter.logProjectEvent(project, "PROJECT_CLOSED")
    }
}