package deshel.valentyn.studylogger.ui.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class StudyLoggerStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String {
        return "StudyLoggerStatusBarWidget"
    }
    override fun getDisplayName(): String {
        return "Study Integrity Logger"
    }
    override fun isAvailable(project: Project): Boolean {
        return true
    }
    override fun createWidget(project: Project): StatusBarWidget {
        return StudyLoggerStatusBarWidget(project)
    }
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
    override fun canBeEnabledOn(statusBar: com.intellij.openapi.wm.StatusBar): Boolean {
        return true
    }
}