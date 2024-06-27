package io.nerdythings.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import io.nerdythings.action.ActionGptRequestHelper


object GptRequestUtil {
    fun makeGPTRequest(project: Project, questionText: String, progressText: String) {
        ActionGptRequestHelper.makeGPTRequest(project, questionText, progressText) { text ->
            GptResponseUtil.openResponseInNewEditor(
                project,text ?: "Nothing was returned from GPT after the request was sent."
            )
        }
    }

    fun updateActionIcon(event: AnActionEvent) {
        IdeaUtil.setActionIcon(event)
    }
}