package com.drvector

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager

import java.util.Arrays

class ActionFixVectorFloatingPoints : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        val document = editor!!.document

        if (!isValidVectorDrawableFile(document)) {
            showDialogNotAVectorDrawable(project)
            return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            var content = document.text
            content = com.drvector.VectorDrawableFixinator.getContentWithFixedFloatingPoints(content)
            document.setText(content)

            formatDocumentAccordingToCodeStyle(document, project)
        }
    }

    private fun formatDocumentAccordingToCodeStyle(document: Document, project: Project?) {
        val file = PsiDocumentManager.getInstance(project!!).getPsiFile(document)
        val codeStyleManager = CodeStyleManager.getInstance(project)
        codeStyleManager.reformatText(file!!, Arrays.asList<TextRange>(file.textRange))
    }

    private fun showDialogNotAVectorDrawable(project: Project?) {
        Messages.showMessageDialog(project, "This file is not a vector drawable", "Oops!", Messages.getInformationIcon())
    }

    private fun isValidVectorDrawableFile(document: Document): Boolean {
        val content = document.text
        return content.contains("<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"")
    }

    override fun update(e: AnActionEvent?) {
        //Get required data keys
        val project = e!!.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)
        //Set visibility only in case of existing project and editor
        e.presentation.isVisible = project != null && editor != null
    }
}
