package cz.findtranslation

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.intellij.xml.XmlText

class TranslationReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(psiElement(PsiLiteralExpression::class.java), JsLiteralProvider())
        registrar.registerReferenceProvider(psiElement(JSLiteralExpression::class.java), JsLiteralProvider())
        registrar.registerReferenceProvider(psiElement(XmlText::class.java), AngularTemplateProvider())
    }
}

private class JsLiteralProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val containingFile = element.containingFile ?: return PsiReference.EMPTY_ARRAY
        val settings = TranslationSettingsState.getInstance(element.project)
        if (!TranslationKeyUtil.fileExtensionMatches(containingFile, settings.sourceExtensions())) return PsiReference.EMPTY_ARRAY

        val key = when (element) {
            is PsiLiteralExpression -> element.value as? String
            is JSLiteralExpression -> element.stringValue
            else -> null
        }?.trim() ?: return PsiReference.EMPTY_ARRAY

        if (!key.contains('.')) return PsiReference.EMPTY_ARRAY

        val text = element.text
        if (text.length < 2) return PsiReference.EMPTY_ARRAY
        if (!(text.startsWith("\"") || text.startsWith("'"))) return PsiReference.EMPTY_ARRAY

        val range = TextRange(1, text.length - 1)
        return arrayOf(TranslationKeyReference(element, range, key))
    }
}

private class AngularTemplateProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val containingFile = element.containingFile ?: return PsiReference.EMPTY_ARRAY
        val settings = TranslationSettingsState.getInstance(element.project)
        if (!TranslationKeyUtil.fileExtensionMatches(containingFile, settings.sourceExtensions())) return PsiReference.EMPTY_ARRAY

        val xmlText = element as? XmlText ?: return PsiReference.EMPTY_ARRAY
        val results = TranslationKeyUtil.extractAngularKeyRanges(xmlText.text)
        if (results.isEmpty()) return PsiReference.EMPTY_ARRAY

        return results.map { (key, range) -> TranslationKeyReference(element, range, key) }.toTypedArray()
    }
}
