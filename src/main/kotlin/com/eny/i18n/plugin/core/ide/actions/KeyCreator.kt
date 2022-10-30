package com.eny.i18n.plugin.core.ide.actions

import com.eny.i18n.plugin.core.ide.quickfix.CreateKeyQuickFix
import com.eny.i18n.plugin.core.ide.quickfix.CreateTranslationFileQuickFix
import com.eny.i18n.plugin.core.ide.quickfix.UserChoice
import com.eny.i18n.plugin.core.localization.source.LocalizationSourceService
import com.eny.i18n.plugin.factory.TranslationExtractor
import com.eny.i18n.plugin.ide.settings.Settings
import com.eny.i18n.plugin.key.FullKey
import com.eny.i18n.plugin.localization.json.JsonLocalizationFactory
import com.eny.i18n.plugin.localization.yaml.YamlLocalizationFactory
import com.eny.i18n.plugin.utils.PluginBundle
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

/**
 * Extracts translation key
 */
class KeyCreator {

    /**
     * Tries to resolve translation file
     */
    fun createKey(project:Project, i18nKey: FullKey, source: String, editor:Editor, extractor: TranslationExtractor, onComplete: () -> Unit) {
        val sourceService = project.service<LocalizationSourceService>()
        val settings = Settings.getInstance(project)
        val config = settings.config()
        val files = sourceService.findSources(i18nKey.allNamespaces(), project)
        val generators = settings.mainFactory().contentGenerators()
        val quickFix = if (files.isEmpty()) {
            val contentGenerator = if (config.preferYamlFilesGeneration) //TODO
                YamlLocalizationFactory().contentGenerator() else
                JsonLocalizationFactory().contentGenerator()
            val fileName = i18nKey.ns?.text ?: config.defaultNamespaces().first()
            CreateTranslationFileQuickFix(i18nKey, contentGenerator, fileName, source, onComplete)
        } else {
            CreateKeyQuickFix(i18nKey, UserChoice(), PluginBundle.getMessage("quickfix.create.key"), generators, source, onComplete)
        }
        quickFix.invoke(project, editor)
    }
}
