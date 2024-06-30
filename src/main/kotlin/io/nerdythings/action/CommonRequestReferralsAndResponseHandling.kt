package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import io.nerdythings.api.GptRepository
import io.nerdythings.dialog.SelectReferredCodeDialog
import io.nerdythings.preferences.AppSettingsState
import io.nerdythings.utils.FileUtil
import io.nerdythings.utils.GptRequestUtil
import io.nerdythings.utils.IdeaUtil
import io.nerdythings.utils.UserResponseUtil
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NotNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

abstract class CommonRequestReferralsAndResponseHandling(private val dialogTitle: String,
    private var doUpdateSettingsPrompt: Boolean = false) : AnAction() {
        private lateinit var promptToAi: String

        fun initialize(paramPromptToAi: String) {
            this.promptToAi = paramPromptToAi
        }

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        val project = event.project
        val editor = event.getData(CommonDataKeys.EDITOR)
        if (!UserResponseUtil.validateInputUsable(project, editor)) {
            return
        }
        val currentFile = FileUtil.determineLocalFile(event)
        val dialog = SelectReferredCodeDialog(promptToAi, dialogTitle, doUpdateSettingsPrompt, currentFile)
        dialog.show()
        if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
            handleDialogOk(event, project!!, editor!!, dialog)
        } else {
            // Handle dialog cancel or close action if needed
        }
    }

    private fun handleDialogOk(event: AnActionEvent, project: Project, editor: Editor,
                               dialog: SelectReferredCodeDialog) {
        val settings = AppSettingsState.instance
        promptToAi = dialog.getUpdatedPrompt()
        val questionText = StringBuilder(promptToAi)
        val filesToSend = mutableListOf<File>()
        val sendCodeMethod = settings.shouldSendCode()
        val progressText = "$dialogTitle in progress..."

        runBlocking {
            if (sendCodeMethod == AppSettingsState.SendCodeMethod.SEND_A_FILE) {
                UserResponseUtil.handleSendFile(event, editor, project, questionText, filesToSend, progressText)
            } else if (sendCodeMethod == AppSettingsState.SendCodeMethod.SEND_FILE_AND_OTHERS) {
                UserResponseUtil.handleSendFileAndOthers(event, editor, project, questionText, filesToSend, settings, progressText)
            } else if (sendCodeMethod == AppSettingsState.SendCodeMethod.SEND_SELECTED_ONLY) {
                UserResponseUtil.handleSendSelectedOnly(editor, project, questionText, progressText)
            } else {
                GptRequestUtil.makeGPTRequest(project, questionText.toString(), progressText)
            }
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
        GptRequestUtil.makeGPTRequest(project, referencedQuestion,"Asking GPT...")
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
        private val DATE_FORMAT = SimpleDateFormat("yyyyMMdd").format(Date())
        private const val COMMENT_START = "/**"
        private const val COMMENT_END = "*/"
    }
}