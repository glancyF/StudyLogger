package deshel.valentyn.studylogger.ui.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import deshel.valentyn.studylogger.logic.writer.StudySubmissionEvidenceWriter
import java.nio.file.Path

class GenerateSubmissionEvidenceAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return

        val descriptor = FileChooserDescriptor(
            true,
            false,
            true,
            true,
            false,
            false
        ).withTitle("Select Submission File or Archive")

        val selectedFile = FileChooser.chooseFile(descriptor, project, null)

        if (selectedFile == null) {
            return
        }

        val selectedPath = Path.of(selectedFile.path)

        StudySubmissionEvidenceWriter.writeSubmissionEvidence(project, selectedPath)

        Messages.showInfoMessage(
            project,
            "Submission evidence created in .study-log/submission-evidence.txt",
            "Study Integrity Logger"
        )
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }
}