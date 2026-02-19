package cz.findtranslation

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class TranslationConfigurable(private val project: Project) : Configurable {
    private val sourceExtensionsField = JBTextField()
    private val translationExtensionsField = JBTextField()
    private var panel: JPanel? = null

    override fun getDisplayName(): String = "Find Translation"

    override fun createComponent(): JComponent {
        val settings = TranslationSettingsState.getInstance(project)
        sourceExtensionsField.text = settings.state.sourceFilePatterns
        translationExtensionsField.text = settings.state.translationFilePatterns

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Source file extensions (comma separated):"), sourceExtensionsField)
            .addLabeledComponent(JBLabel("Translation file extensions (comma separated):"), translationExtensionsField)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return panel as JPanel
    }

    override fun isModified(): Boolean {
        val settings = TranslationSettingsState.getInstance(project)
        return sourceExtensionsField.text != settings.state.sourceFilePatterns ||
            translationExtensionsField.text != settings.state.translationFilePatterns
    }

    override fun apply() {
        val settings = TranslationSettingsState.getInstance(project)
        settings.state.sourceFilePatterns = sourceExtensionsField.text
        settings.state.translationFilePatterns = translationExtensionsField.text
    }
}
