package io.nerdythings.action

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.nerdythings.api.GptRepository
import kotlinx.coroutines.runBlocking


object ActionGptRequestHelper {

    fun makeGPTRequest(
        project: Project,
        question: String,
        progressText: String = "Running Task",
        result: (String) -> Unit
    ) {
        ProgressManager.getInstance().run(object : Task.Modal(project, progressText, true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                runBlocking {
                    GptRepository.askGpt(question) { response, error ->
                        if (error == null) {
                            if (response == null) {
                                Messages.showMessageDialog(
                                    project,
                                    "Empty response",
                                    "Error",
                                    Messages.getInformationIcon()
                                )
                            } else {
                                result.invoke(response)
                            }
                        } else {
                            Messages.showMessageDialog(project, error, "Error", Messages.getInformationIcon())
                        }
                    }
                }
            }
        })
    }

    fun cutCodeFromResponse(response: String): String {
        val regex = "```.*?\\n(.*?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
        return regex.findAll(response)
            .map { matchResult ->
                matchResult.groups[1]?.value?.trim() ?: ""
            }
            .toList().joinToString("\n")
    }
}