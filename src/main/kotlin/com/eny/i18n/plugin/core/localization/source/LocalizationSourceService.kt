package com.eny.i18n.plugin.core.localization.source

import com.eny.i18n.plugin.utils.LocalizationSource
import com.eny.i18n.plugin.utils.LocalizationSourceSearch
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement

@Service
class LocalizationSourceService {

    fun findSources(allNamespaces: List<String>, element: PsiElement): List<LocalizationSource> {
        return LocalizationSourceSearch(element.project).findSources(allNamespaces, element)
    }

    fun findFilesByHost(allNamespaces: List<String>, container: PsiElement): List<LocalizationSource> {
        return LocalizationSourceSearch(container.project).findFilesByHost(allNamespaces, container)
    }
}