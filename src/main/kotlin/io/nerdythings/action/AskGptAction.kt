package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import io.nerdythings.dialog.AskGptDialog
import io.nerdythings.preferences.AppSettingsState
import io.nerdythings.utils.IdeaUtil
import org.jetbrains.annotations.NotNull


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
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText ?: ""
        val dialog = AskGptDialog()
        dialog.show()
        if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
            val questionText = StringBuilder(AppSettingsState.instance.gptAsk)

            if (AppSettingsState.instance.shouldSendCode() == AppSettingsState.SendCodeMethod.SEND_A_FILE) {
                questionText.append("\nCode:\n")
                val parsedEvent = ActionHelper.parseEvent(event)
                questionText.append(parsedEvent.code)
            } else if (
                AppSettingsState.instance.shouldSendCode() == AppSettingsState.SendCodeMethod.SEND_SELECTED_ONLY
            ) {
                questionText.append("\nCode:\n$selectedText")
            }
            // OK button was pressed
            ActionGptRequestHelper.makeGPTRequest(project, questionText.toString(), "Asking GPT...") { text ->
                IdeaUtil.insertIntoSameFile(
                    project = project,
                    editor = editor,
                    text = COMMENT_START +
                            SEPARATOR +
                            text +
                            SEPARATOR +
                            COMMENT_END,
                )
            }
        } else {
            // Dialog was cancelled or closed
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