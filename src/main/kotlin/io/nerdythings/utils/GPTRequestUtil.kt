package io.nerdythings.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import io.nerdythings.api.GptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File


object GptRequestUtil {
    fun makeGPTRequest(project: Project, questionText: String, progressText: String) {
        runGptRequest(project, progressText, { callback ->
            GptRepository.askGpt(questionText, callback)
        }) { text ->
            GptResponseUtil.openResponseInNewEditor(
                project,text ?: "Nothing was returned from GPT after the request was sent."
            )
        }
    }

    internal fun runGptRequest(
        project: Project,
        progressText: String,
        request: suspend (callback: (String?, String?) -> Unit) -> Unit,
        result: (String?) -> Unit
    ) {
        ProgressManager.getInstance().run(object : Task.Modal(project, progressText, true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                runBlocking {
                    withContext(Dispatchers.IO) {
                        request { response, error ->
                            GptResponseUtil.ensureErrorFreeResponseWithContent(project, response, error, result)
                        }
                    }
                }
            }
        })
    }

    fun makeGPTRequestWithFiles(
        project: Project,
        question: String,
        files: List<File>,
        progressText: String = "Running Task",
        result: (String?) -> Unit
    ) {
        runGptRequest(project, progressText, { callback ->
            GptRepository.askGptAddingFilesToRequest(question, files, callback)
        }, result)
    }

    fun updateActionIcon(event: AnActionEvent) {
        IdeaUtil.setActionIcon(event)
    }
}