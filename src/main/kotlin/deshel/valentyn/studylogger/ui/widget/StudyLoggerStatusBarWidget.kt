package deshel.valentyn.studylogger.ui.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.wm.StatusBarWidget
import deshel.valentyn.studylogger.logic.state.StudyLoggerState
import deshel.valentyn.studylogger.logic.writer.StudyLogWriter

class StudyLoggerStatusBarWidget(project: Project) : EditorBasedStatusBarPopup(project, false) {
    override fun createInstance(project: Project): StatusBarWidget {
        return StudyLoggerStatusBarWidget(project)
    }
    override fun ID(): String {
        return "StudyLoggerStatusBarWidget"
    }

    override fun getWidgetState(file: com.intellij.openapi.vfs.VirtualFile?): WidgetState {
        val text = if (StudyLoggerState.isEnabled()) {
            "Study Logger: ON"
        } else {
            "Study Logger: OFF"
        }

        return WidgetState(
            "Toggle Study Logger",
            text,
            true
        )
    }

    override fun createPopup(context: com.intellij.openapi.actionSystem.DataContext): com.intellij.openapi.ui.popup.ListPopup {
        val group = DefaultActionGroup()

        group.add(object : AnAction(
            if (StudyLoggerState.isEnabled()) "Turn Study Logger OFF" else "Turn Study Logger ON"
        ) {
            override fun actionPerformed(event: AnActionEvent) {
                val enabled = StudyLoggerState.toggle()

                if (enabled) {
                    StudyLogWriter.logPluginStateEvent(project, "PLUGIN_ENABLED")
                } else {
                    StudyLogWriter.logPluginStateEvent(project, "PLUGIN_DISABLED")
                }

                update()
            }
        })

        return com.intellij.openapi.ui.popup.JBPopupFactory.getInstance()
            .createActionGroupPopup(
                "Study Integrity Logger",
                group,
                context,
                com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                true
            )
    }
}