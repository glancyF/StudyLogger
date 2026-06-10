package deshel.valentyn.studylogger.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import deshel.valentyn.studylogger.writer.StudyFinalReportWriter

class GenerateFinalReportAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return
        StudyFinalReportWriter.writeFinalReport(project)
        Messages.showInfoMessage(
            project,
            "Final report created in .study-log/session-final.json",
            "Study Integrity Logger"
        )
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }
}