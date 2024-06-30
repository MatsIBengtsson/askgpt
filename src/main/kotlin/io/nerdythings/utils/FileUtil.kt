package io.nerdythings.utils

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.io.IOException

object FileUtil {

    fun appendFilesAsContentAndAddFilesToList(event: AnActionEvent, editor: Editor, project: Project?, questionText: StringBuilder,
                                              filesToSend: MutableList<File>): Boolean {
        if (!isCurrentTabSaved(editor)) {
            Messages.showMessageDialog(
                project,
                "The currently selected tab is not connected to a file. Please use the 'selected text' option instead.",
                "Warning", Messages.getWarningIcon()
            )
            return false
        }
        questionText.append("\nFiles with code:\n")
        val localFilePath = determineLocalFilePath(event)
        if (localFilePath != null) {
            filesToSend.add(File(localFilePath))
        }
        return true
    }

    fun appendFilesInListToQuestion(questionText: StringBuilder, filesToSend: List<File>) {
        filesToSend.forEach { file ->
            questionText.append("filename: ${file.name}\n")
            questionText.append(file.readText())
            questionText.append("\n")
        }
    }

    private fun isCurrentTabSaved(editor: Editor): Boolean {
        val document = editor.document
        val virtualFile = FileDocumentManager.getInstance().getFile(document)
        return virtualFile != null && virtualFile.isInLocalFileSystem
    }

    fun determineLocalFilePath(event: AnActionEvent): String? {
        val vFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        return if (vFile != null && vFile.exists() && !vFile.isDirectory) {
            vFile.path
        } else {
            null
        }
    }

    fun determineLocalFile(event: AnActionEvent): File? {
        val localFilePath = determineLocalFilePath(event)
        return if (localFilePath != null) {
            File(localFilePath)
        } else {
            null
        }
    }

    fun getVirtualFileContent(event: AnActionEvent): String? {
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        if (file != null) {
            val content = readFileContentForExistingVirtualFile(file)
            return content
        } else {
            return null
        }
    }

    fun readFileContentForExistingVirtualFile(file: VirtualFile): String? {
        return try {
            String(file.contentsToByteArray(), file.charset)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}