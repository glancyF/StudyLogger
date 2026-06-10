package deshel.valentyn.studylogger.state

object StudyLoggerState {
    private var enabled: Boolean = true

    fun isEnabled(): Boolean {
        return enabled
    }

    fun setEnabled(value: Boolean) {
        enabled = value
    }

    fun toggle(): Boolean {
        enabled = !enabled
        return enabled
    }
}