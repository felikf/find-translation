package cz.findtranslation

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveResultImpl

class TranslationKeyReference(
    element: PsiElement,
    rangeInElement: TextRange,
    private val key: String
) : PsiPolyVariantReferenceBase<PsiElement>(element, rangeInElement) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val targets = TranslationKeyUtil.findTranslationTargets(element.project, key)
        return targets.map { ResolveResultImpl(it) }.toTypedArray()
    }

    override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.element
}
