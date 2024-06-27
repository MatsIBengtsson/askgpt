package io.nerdythings.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.nerdythings.action.ActionHelper
import java.io.File

object FileUtil {

    fun appendFilesAsContentAndAddFilesToList(event: AnActionEvent, editor: Editor, project: Project?, questionText: StringBuilder, filesToSend: MutableList<File>) {
        if (!isCurrentTabSaved(editor)) {
            Messages.showMessageDialog(
                project,
                "The currently selected tab is not connected to a file. Please use the 'selected text' option instead.",
                "Warning",
                Messages.getWarningIcon()
            )
            return
        }
        questionText.append("\nFiles with code:\n")
        val parsedEvent = ActionHelper.parseEvent(event)
        val localFilePath = parsedEvent.localFilePath
        if (localFilePath != null) {
            filesToSend.add(File(localFilePath))
        }
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
}