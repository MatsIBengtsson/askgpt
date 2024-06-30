package io.nerdythings.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.nerdythings.preferences.AppSettingsState
import java.io.File

object UserResponseUtil {

    fun validateInputUsable(project: Project?, editor: Editor?): Boolean {
        if (project == null || editor == null) {
            Messages.showMessageDialog(project,"Can't get code. Please try another file.",
                "Error", Messages.getInformationIcon())
            return false
        }
        return true
    }

    internal suspend fun handleSendFile(event: AnActionEvent, editor: Editor, project: Project, questionText: StringBuilder,
                                        filesToSend: MutableList<File>, progressText: String): Boolean {
        if (!FileUtil.appendFilesAsContentAndAddFilesToList(event, editor, project, questionText, filesToSend))
            return false
        FileUtil.appendFilesInListToQuestion(questionText, filesToSend)
        GptRequestUtil.makeGPTRequest(project, questionText.toString(), progressText)
        return true
    }

    internal suspend fun handleSendFileAndOthers(event: AnActionEvent, editor: Editor, project: Project, questionText: StringBuilder,
                                                 filesToSend: MutableList<File>, settings: AppSettingsState, progressText: String): Boolean {
        if (!FileUtil.appendFilesAsContentAndAddFilesToList(event, editor, project, questionText, filesToSend))
            return false
        filesToSend.addAll(settings.additionalFiles.map { File(it) })
        FileUtil.appendFilesInListToQuestion(questionText, filesToSend)
        GptRequestUtil.makeGPTRequest(project, questionText.toString(), progressText)
        return true
    }

    suspend fun handleSendSelectedOnly(editor: Editor, project: Project, questionText: StringBuilder, progressText: String) {
        val selectedText = editor.selectionModel.selectedText ?: ""
        questionText.append("\nCode:\n$selectedText")
        GptRequestUtil.makeGPTRequest(project, questionText.toString(), progressText)
    }
}