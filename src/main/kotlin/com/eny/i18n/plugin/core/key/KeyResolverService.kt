package com.eny.i18n.plugin.core.key

import com.eny.i18n.plugin.tree.CompositeKeyResolver
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement

@Service
class KeyResolverService: CompositeKeyResolver<PsiElement>