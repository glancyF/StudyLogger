package deshel.valentyn.studylogger.logic.state

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(
    name = "StudyLoggerState",
    storages = [Storage("studyLoggerState.xml")]
)
class StudyLoggerState : PersistentStateComponent<StudyLoggerState.State> {

    data class State(
        var enabled: Boolean = true
    )

    private var state = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    fun isEnabled(): Boolean {
        return state.enabled
    }

    fun setEnabled(value: Boolean) {
        state.enabled = value
    }

    fun toggle(): Boolean {
        state.enabled = !state.enabled
        return state.enabled
    }

    companion object {
        fun getInstance(): StudyLoggerState {
            return ApplicationManager.getApplication().getService(StudyLoggerState::class.java)
        }
    }
}