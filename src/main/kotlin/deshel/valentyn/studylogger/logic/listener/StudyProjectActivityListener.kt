package deshel.valentyn.studylogger.logic.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import deshel.valentyn.studylogger.logic.writer.StudyLogWriter
import deshel.valentyn.studylogger.logic.writer.StudySessionInfoWriter

class StudyProjectActivityListener : ProjectManagerListener {
    override fun projectOpened(project: Project) {
        StudyLogWriter.logProjectEvent(project, "PROJECT_OPENED")
        StudySessionInfoWriter.writeSessionInfo(project)
    }

    override fun projectClosing(project: Project) {
        StudyLogWriter.logProjectEvent(project, "PROJECT_CLOSED")
    }
}