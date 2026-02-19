package cz.findtranslation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.ui.Messages

class FindTranslationKeyAction : AnAction("Find Translation Key") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val key = Messages.showInputDialog(
            project,
            "Enter translation key (e.g. pages.incomeAssessment.confirm):",
            "Find Translation Key",
            Messages.getQuestionIcon()
        )?.trim().orEmpty()

        if (key.isBlank()) return

        val targets = TranslationKeyUtil.findTranslationTargets(project, key)
        if (targets.isEmpty()) {
            Messages.showInfoMessage(project, "Translation key '$key' was not found.", "Find Translation Key")
            return
        }

        if (targets.size == 1) {
            openTarget(project, targets.first())
            return
        }

        val options = targets.mapNotNull {
            val file = it.containingFile?.virtualFile ?: return@mapNotNull null
            "${file.path}:${it.textOffset + 1}"
        }.toTypedArray()

        val selected = Messages.showChooseDialog(
            project,
            "Multiple translation declarations found for '$key':",
            "Find Translation Key",
            options,
            options.firstOrNull(),
            Messages.getQuestionIcon()
        )

        val selectedIdx = options.indexOf(selected)
        if (selectedIdx >= 0) openTarget(project, targets[selectedIdx])
    }

    private fun openTarget(project: com.intellij.openapi.project.Project, target: com.intellij.psi.PsiElement) {
        val file = target.containingFile?.virtualFile ?: return
        OpenFileDescriptor(project, file, target.textOffset).navigate(true)
    }
}
