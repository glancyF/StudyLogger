package deshel.valentyn.studylogger.ui.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import deshel.valentyn.studylogger.logic.state.StudyLoggerState
import deshel.valentyn.studylogger.logic.writer.StudyLogWriter

class ToggleStudyLoggerAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return

        val enabled = StudyLoggerState.getInstance().toggle()

        if (enabled) {
            StudyLogWriter.logPluginStateEvent(project, "PLUGIN_ENABLED")
        } else {
            StudyLogWriter.logPluginStateEvent(project, "PLUGIN_DISABLED")
        }

        Messages.showInfoMessage(
            project,
            "Study Logger is now ${if (enabled) "ON" else "OFF"}",
            "Study Integrity Logger"
        )
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
        event.presentation.text = if (StudyLoggerState.getInstance().isEnabled()) {
            "Turn Study Logger OFF"
        } else {
            "Turn Study Logger ON"
        }
    }
}