package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import io.nerdythings.utils.IdeaUtil

class NerdyActionGroup : DefaultActionGroup() {
    override fun update(event: AnActionEvent) {
        IdeaUtil.setActionIcon(event)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}