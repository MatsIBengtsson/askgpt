package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import io.nerdythings.utils.IdeaUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import io.nerdythings.preferences.AppSettingsState
import org.jetbrains.annotations.NotNull


class FindBugsAction : AnAction() {

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        val project = event.project
        val editor = event.getData(CommonDataKeys.EDITOR)
        if (project == null || editor == null) {
            Messages.showMessageDialog(
                project,
                "Something went wrong. Sorry :)",
                "Error",
                Messages.getInformationIcon()
            )
            return
        }

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

        ActionGptRequestHelper.makeGPTRequest(
            project,
            AppSettingsState.instance.checkBugsQuestion + "\nCode:\n" + actionRequest.code,
            "GPT is looking in your code..."
        ) {
            IdeaUtil.replaceFileContent(project, editor, it)
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