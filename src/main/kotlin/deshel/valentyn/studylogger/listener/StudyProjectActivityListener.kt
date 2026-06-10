package deshel.valentyn.studylogger.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import deshel.valentyn.studylogger.writer.StudyLogWriter
import deshel.valentyn.studylogger.writer.StudySessionInfoWriter

class StudyProjectActivityListener : ProjectManagerListener {
    override fun projectOpened(project: Project) {
        StudyLogWriter.logProjectEvent(project, "PROJECT_OPENED")
        StudySessionInfoWriter.writeSessionInfo(project)
    }

    override fun projectClosing(project: Project) {
        StudyLogWriter.logProjectEvent(project, "PROJECT_CLOSED")
    }
}