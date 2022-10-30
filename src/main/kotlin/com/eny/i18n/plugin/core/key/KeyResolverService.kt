package com.eny.i18n.plugin.core.key

import com.eny.i18n.plugin.tree.CompositeKeyResolver
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement

@Service
class KeyResolverService: CompositeKeyResolver<PsiElement> {

    fun groupPlurals(completions: List<String>, pluralSeparator: String):List<String> =
        completions.groupBy {it.substringBeforeLast(pluralSeparator)}
            //TODO: technology specific
            .entries.flatMap {
                    entry -> if(entry.value.size == 3 && entry.value.containsAll(listOf(1,2,5).map{entry.key+pluralSeparator+it})) {
                listOf(entry.key)} else entry.value
            }
}