package com.eny.i18n.plugin.core.key

import com.eny.i18n.plugin.factory.CallContext
import com.eny.i18n.plugin.ide.settings.Settings
import com.eny.i18n.plugin.key.FullKey
import com.eny.i18n.plugin.key.parser.KeyParserBuilder
import com.eny.i18n.plugin.parser.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

/**
 * Extracts translation key from psi element
 */
@Service
class KeyExtractor {

    fun extractFullKey(context: CallContext, element: PsiElement): FullKey? {
        return if (context.accepts(element)) extractFullKey(element)
        else null
    }

    /**
     * Extracts fullkey from element, if possible
     */
    private fun extractFullKey(element: PsiElement): FullKey? {
        val config = Settings.getInstance(element.project).config()
        val parser = (
                if (config.gettext)
                    KeyParserBuilder.withoutTokenizer()
                else
                    KeyParserBuilder
                        .withSeparators(config.nsSeparator, config.keySeparator)
                        .withDummyNormalizer()
                        .withTemplateNormalizer()
                ).build()
        return listOf(//TODO
            ReactUseTranslationHookExt(),
            TemplateKeyExt(),
            LiteralKeyExt(),
            StringLiteralKeyExt(),
            XmlAttributeKeyExt()
        )
            .find {it.canExtract(element)}
            ?.let{parser.parse(it.extract(element), config.gettext, config.firstComponentNs)}
    }
}