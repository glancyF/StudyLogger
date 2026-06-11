package deshel.valentyn.studylogger.ui.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import deshel.valentyn.studylogger.logic.writer.StudyEvidencePackageExporter

class ExportEvidencePackageAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return

        val zipFile = StudyEvidencePackageExporter.exportEvidencePackage(project)

        if (zipFile == null) {
            Messages.showErrorDialog(
                project,
                "Could not export evidence package.",
                "Study Integrity Logger"
            )
            return
        }

        Messages.showInfoMessage(
            project,
            "Evidence package created:\n$zipFile",
            "Study Integrity Logger"
        )
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }
}