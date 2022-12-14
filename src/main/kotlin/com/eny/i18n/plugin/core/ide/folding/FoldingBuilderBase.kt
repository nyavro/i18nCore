package com.eny.i18n.plugin.core.ide.folding

import com.eny.i18n.plugin.core.key.KeyResolverService
import com.eny.i18n.plugin.core.localization.source.LocalizationSourceService
import com.eny.i18n.plugin.factory.LanguageFactory
import com.eny.i18n.plugin.ide.settings.Config
import com.eny.i18n.plugin.ide.settings.Settings
import com.eny.i18n.plugin.key.FullKey
import com.eny.i18n.plugin.key.parser.KeyParserBuilder
import com.eny.i18n.plugin.tree.CompositeKeyResolver
import com.eny.i18n.plugin.tree.PropertyReference
import com.eny.i18n.plugin.tree.PsiElementTree
import com.eny.i18n.plugin.utils.KeyElement
import com.eny.i18n.plugin.utils.LocalizationSourceSearch
import com.eny.i18n.plugin.utils.ellipsis
import com.eny.i18n.plugin.utils.unQuote
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement

internal data class ElementToReferenceBinding(val psiElement: PsiElement, val reference: PropertyReference<PsiElement>)

/**
 * Provides folding mechanism for i18n keys
 */
abstract class FoldingBuilderBase(private val languageFactory: LanguageFactory) : FoldingBuilderEx(), DumbAware {

    private val group = FoldingGroup.newGroup("i18n")

    override fun getPlaceholderText(node: ASTNode): String? = ""

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val config = Settings.getInstance(root.project).config()
        val parser = (
            if (config.gettext) KeyParserBuilder.withoutTokenizer()
            else KeyParserBuilder.withSeparators(config.nsSeparator, config.keySeparator).withTemplateNormalizer()
        ).build()
        if (!config.foldingEnabled) return arrayOf()
        val foldingProvider = languageFactory.foldingProvider()
        val search = root.project.service<LocalizationSourceService>()
        val keyResolverService = root.project.service<KeyResolverService>()
        return foldingProvider.collectContainers(root)
            .flatMap { container ->
                val (literals, offset) = foldingProvider.collectLiterals(container)
                literals.mapNotNull { literal ->
                    parser.parse(Pair(listOf(KeyElement.literal(literal.text.unQuote())), null), config.gettext, config.firstComponentNs)
                        ?.let { key -> resolve(container, literal, search, config, key, keyResolverService) }
                        ?.let { resolved ->
                            FoldingDescriptor(
                                container.node,
                                foldingProvider.getFoldingRange(container, offset, resolved.psiElement),
                                group,
                                resolved.reference.element?.value()?.text?.unQuote()?.ellipsis(config.foldingMaxLength) ?: ""
                            )
                        }
                }
            }.toTypedArray()
    }

    private fun resolve(container: PsiElement, element: PsiElement, sourceService: LocalizationSourceService, config: Config, fullKey: FullKey, resolver: KeyResolverService): ElementToReferenceBinding? {
        return sourceService
            .findFilesByHost(fullKey.allNamespaces(), container)
            .filter {
                it.parent == config.foldingPreferredLanguage
            }
            .map { resolver.resolveCompositeKey(fullKey.compositeKey, PsiElementTree.create(it.element), it.type)}
            .firstOrNull { it.unresolved.isEmpty() && it.element?.isLeaf() == true }
            ?.let { ElementToReferenceBinding(element, it) }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = true
}

