package cz.findtranslation

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLMapping

object TranslationKeyUtil {
    private val reactCallPattern = Regex("""\b(?:t|i18n\.t|translate)\s*\(\s*(['\"])([\w.-]+)\1""")
    private val angularPipePattern = Regex("""(['\"])([\w.-]+)\1\s*\|\s*translate\b""")

    fun extractReactKeyWithRange(text: String): Pair<String, TextRange>? {
        val match = reactCallPattern.find(text) ?: return null
        val key = match.groupValues[2]
        val keyStart = match.range.first + match.value.indexOf(key)
        return key to TextRange(keyStart, keyStart + key.length)
    }

    fun extractAngularKeyRanges(text: String): List<Pair<String, TextRange>> {
        return angularPipePattern.findAll(text).map {
            val key = it.groupValues[2]
            val keyStart = it.range.first + it.value.indexOf(key)
            key to TextRange(keyStart, keyStart + key.length)
        }.toList()
    }

    fun findTranslationTargets(project: Project, key: String): List<PsiElement> {
        val settings = TranslationSettingsState.getInstance(project)
        val scope = GlobalSearchScope.projectScope(project)
        val targets = mutableListOf<PsiElement>()

        for (ext in settings.translationExtensions()) {
            val files: Collection<VirtualFile> = FilenameIndex.getAllFilesByExt(project, ext, scope)
            for (vf in files) {
                val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) ?: continue
                val resolved = when (psiFile) {
                    is JsonFile -> findInJson(psiFile, key)
                    is YAMLFile -> findInYaml(psiFile, key)
                    else -> null
                }
                if (resolved != null) {
                    targets.add(resolved)
                }
            }
        }

        return targets
    }

    private fun findInJson(file: JsonFile, key: String): PsiElement? {
        val segments = key.split('.')
        val topObject = PsiTreeUtil.getChildOfType(file, JsonObject::class.java) ?: return null
        var currentObject: JsonObject = topObject
        var property: JsonProperty? = null

        for ((index, segment) in segments.withIndex()) {
            property = currentObject.findProperty(segment) ?: return null
            if (index < segments.lastIndex) {
                currentObject = property.value as? JsonObject ?: return null
            }
        }

        return property?.nameElement ?: property
    }

    private fun findInYaml(file: YAMLFile, key: String): PsiElement? {
        val segments = key.split('.')
        var mapping: YAMLMapping = file.documents.firstOrNull()?.topLevelValue as? YAMLMapping ?: return null
        var keyValue: YAMLKeyValue? = null

        for ((index, segment) in segments.withIndex()) {
            keyValue = mapping.getKeyValueByKey(segment) ?: return null
            if (index < segments.lastIndex) {
                mapping = keyValue.value as? YAMLMapping ?: return null
            }
        }

        return keyValue?.key ?: keyValue
    }

    fun fileExtensionMatches(file: PsiFile, allowedExt: Set<String>): Boolean {
        val ext = file.virtualFile?.extension?.lowercase() ?: return false
        return ext in allowedExt
    }
}
