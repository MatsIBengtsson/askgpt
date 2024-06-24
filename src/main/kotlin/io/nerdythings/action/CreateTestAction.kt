package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import io.nerdythings.utils.IdeaUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import io.nerdythings.preferences.AppSettingsState
import org.jetbrains.annotations.NotNull
import java.io.File


class CreateTestAction : AnAction() {

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        // Using the event, create and show a dialog
        val project = event.project ?: return
        val actionRequest = ActionHelper.parseEvent(event)
        if (actionRequest.code == null || actionRequest.localFilePath == null) {
            Messages.showMessageDialog(
                project,
                "Can't get code. Please try another file.",
                "Error",
                Messages.getInformationIcon()
            )
            return
        }
        val path = actionRequest.localFilePath
        val text = AppSettingsState.instance.createTestQuestion + "\nCode:\n" + actionRequest.code
        ActionGptRequestHelper.makeGPTRequest(
            project, text, "GPT is creating tests for your code..."
        ) { response ->
            val code = ActionGptRequestHelper.cutCodeFromResponse(response ?: "No response from GPT when asked to create tests for your code")
            val dot = path.lastIndexOf('.')
            val newPath = path.substring(0, dot) + "Test" + path.substring(dot, path.length)
            IdeaUtil.writeAndOpenFile(project, newPath, code)
        }
    }

    override fun update(e: AnActionEvent) {
        IdeaUtil.setActionIcon(e)
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}