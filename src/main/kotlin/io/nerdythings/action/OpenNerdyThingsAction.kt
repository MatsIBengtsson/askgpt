package io.nerdythings.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import io.nerdythings.utils.IdeaUtil
import org.jetbrains.annotations.NotNull

class OpenNerdyThingsAction : AnAction() {

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        BrowserUtil.open("https://youtube.com/@Nerdy.Things?si=ePGW7vya2NR5Ugei")
    }

    override fun update(e: AnActionEvent) {
        IdeaUtil.setActionIcon(e)
        e.presentation.isEnabledAndVisible = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}