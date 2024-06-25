package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import io.nerdythings.api.GptRepository
import io.nerdythings.dialog.AskGptDialog
import io.nerdythings.preferences.AppSettingsState
import io.nerdythings.utils.FileUtil
import io.nerdythings.utils.GptResponseUtil.openResponseInNewEditor
import io.nerdythings.utils.IdeaUtil
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NotNull
import java.io.File

class AskGptAction : AnAction() {

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        val project = event.project
        val editor = event.getData(CommonDataKeys.EDITOR)
        if (project == null || editor == null) {
            Messages.showMessageDialog(
                project,
                "Can't get code. Please try another file.",
                "Error",
                Messages.getInformationIcon()
            )
            return
        }
        val dialog = AskGptDialog()
        dialog.show()
        val settings = AppSettingsState.instance
        if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
            val questionText = StringBuilder(settings.gptAsk)
            val filesToSend = mutableListOf<File>()
            val sendCodeMethod = settings.shouldSendCode()

            runBlocking {
                if (sendCodeMethod == AppSettingsState.SendCodeMethod.SEND_A_FILE) {
                    FileUtil.appendFilesAsContentAndAddFilesToList(event, questionText, filesToSend)
                    FileUtil.appendFilesInListToQuestion(questionText, filesToSend)
                    ActionGptRequestHelper.makeGPTRequest(project, questionText.toString(), "Asking GPT...") { text ->
                        openResponseInNewEditor(project, text ?: "Nothing was returned from GPT after the request was sent.")
                    }
                } else if (sendCodeMethod == AppSettingsState.SendCodeMethod.SEND_FILE_AND_OTHERS) {
                    FileUtil.appendFilesAsContentAndAddFilesToList(event, questionText, filesToSend)
                    filesToSend.addAll(settings.additionalFiles.map { File(it) })
                    FileUtil.appendFilesInListToQuestion(questionText, filesToSend)
                    ActionGptRequestHelper.makeGPTRequest(project, questionText.toString(), "Asking GPT...") { text ->
                        openResponseInNewEditor(project, text ?: "Nothing was returned from GPT after the request was sent.")
                    }
                } else if (sendCodeMethod == AppSettingsState.SendCodeMethod.SEND_SELECTED_ONLY) {
                    val selectionModel = editor.selectionModel
                    val selectedText = selectionModel.selectedText ?: ""
                    questionText.append("\nCode:\n$selectedText")
                    ActionGptRequestHelper.makeGPTRequest(project, questionText.toString(), "Asking GPT...") { text ->
                        openResponseInNewEditor(project, text ?: "Nothing was returned from GPT after the request was sent.")
                    }
                } else {
                    ActionGptRequestHelper.makeGPTRequest(project, questionText.toString(), "Asking GPT...") { text ->
                        openResponseInNewEditor(project, text ?: "Nothing was returned from GPT after the request was sent.")
                    }
                }
            }
        } else {
            // Dialog was cancelled or closed
        }
    }

    private fun insertResponseIntoEditor(project: Project, editor: Editor, response: String) {
        IdeaUtil.insertIntoSameFile(
            project = project,
            editor = editor,
            text = COMMENT_START +
                    SEPARATOR +
                    response +
                    SEPARATOR +
                    COMMENT_END,
        )
    }

    private suspend fun uploadFilesAndAskGpt(project: Project, editor: Editor, questionText: String, filesToSend: List<File>) {
        val fileIds = GptRepository.uploadFiles(filesToSend)
        val referencedQuestion = "$questionText (uploaded FileIDs): ${fileIds.joinToString(", ")}"
        ActionGptRequestHelper.makeGPTRequest(project, referencedQuestion,"Asking GPT...") { text ->
            insertResponseIntoEditor(project, editor, text ?: "Nothing was returned from GPT after the request was sent.")
        }
    }

    override fun update(e: AnActionEvent) {
        IdeaUtil.setActionIcon(e)
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private companion object {
        private const val SEPARATOR = "\n\n ------------------------------------------------------------------ \n\n"
        private const val COMMENT_START = "/**"
        private const val COMMENT_END = "*/"
    }
}