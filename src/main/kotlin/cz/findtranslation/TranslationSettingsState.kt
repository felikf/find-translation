package cz.findtranslation

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "TranslationSettingsState", storages = [Storage("translation-key-settings.xml")])
class TranslationSettingsState : PersistentStateComponent<TranslationSettingsState.State> {
    data class State(
        var sourceFilePatterns: String = "tsx,jsx,html,ts,js",
        var translationFilePatterns: String = "json,yml,yaml"
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun sourceExtensions(): Set<String> = parseExtensions(state.sourceFilePatterns)

    fun translationExtensions(): Set<String> = parseExtensions(state.translationFilePatterns)

    private fun parseExtensions(csv: String): Set<String> = csv
        .split(',')
        .asSequence()
        .map { it.trim().lowercase() }
        .filter { it.isNotBlank() }
        .toSet()

    companion object {
        fun getInstance(project: Project): TranslationSettingsState = project.getService(TranslationSettingsState::class.java)
    }
}
