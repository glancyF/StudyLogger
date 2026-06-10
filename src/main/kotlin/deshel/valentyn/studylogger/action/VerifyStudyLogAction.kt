package deshel.valentyn.studylogger.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import deshel.valentyn.studylogger.writer.StudyEvidenceReportWriter

class VerifyStudyLogAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return

        StudyEvidenceReportWriter.writeVerificationReport(project)
        Messages.showInfoMessage(
            project,
            "Verification report created in .study-log/verification-report.txt",
            "Study Integrity Logger"
        )
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }
}