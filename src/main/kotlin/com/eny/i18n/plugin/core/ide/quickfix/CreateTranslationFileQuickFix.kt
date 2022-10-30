package com.eny.i18n.plugin.core.ide.quickfix

import com.eny.i18n.plugin.factory.ContentGenerator
import com.eny.i18n.plugin.ide.quickfix.QuickFix
import com.eny.i18n.plugin.key.FullKey
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory

/**
 * Quick fix for translation file creation
 */
class CreateTranslationFileQuickFix(
    private val fullKey: FullKey,
    private val contentGenerator: ContentGenerator,
    private val fileName: String,
    private val translationValue: String? = null,
    private val onComplete: () -> Unit = {}): QuickFix() {

    private val folderSelector = ApplicationManager.getApplication().getService(TranslationFolderSelector::class.java)

    override fun getText(): String = contentGenerator.getDescription()

    override fun invoke(project: Project, editor: Editor) =
        ApplicationManager.getApplication().invokeLater {
            doInvoke(project)
        }

    private fun doInvoke(project: Project) {
        val name: String = fileName + "." + contentGenerator.getType().fileType.defaultExtension
        val content: String = contentGenerator.generateContent(fullKey, translationValue)
        folderSelector.select(project) { folders ->
            WriteCommandAction.runWriteCommandAction(project) {
                folders.forEach {
                    it.add(PsiFileFactory.getInstance(project).createFileFromText(name, contentGenerator.getLanguage(), content))
                }
                onComplete()
            }
        }
    }
}
