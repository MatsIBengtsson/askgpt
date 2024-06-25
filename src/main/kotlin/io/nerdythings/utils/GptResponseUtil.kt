package io.nerdythings.utils

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile

object GptResponseUtil {

    fun openResponseInNewEditor(project: Project, response: String) {
        val document: Document = com.intellij.openapi.editor.EditorFactory.getInstance().createDocument(response)
        val file: VirtualFile = LightVirtualFile("GPT Response", document.text)
        val manager = FileEditorManager.getInstance(project)
        manager.openTextEditor(OpenFileDescriptor(project, file), true)
    }


    fun cutCodeFromResponse(response: String): String {
        val regex = "```.*?\\n(.*?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
        return regex.findAll(response)
            .map { matchResult ->
                matchResult.groups[1]?.value?.trim() ?: ""
            }
            .toList().joinToString("\n")
    }
}

