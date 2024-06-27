package io.nerdythings.action

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.nerdythings.api.GptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object ActionGptRequestHelper {

    fun makeGPTRequest(
        project: Project,
        question: String,
        progressText: String = "Running Task",
        result: (String?) -> Unit
    ) {
        runGptRequest(project, progressText, { callback ->
            GptRepository.askGpt(question, callback)
        }, result)
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

    private fun runGptRequest(
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
                            handleResponse(project, response, error, result)
                        }
                    }
                }
            }
        })
    }

    private fun handleResponse(project: Project, response: String?,
        error: String?, result: (String?) -> Unit) {
        if (error == null) {
            if (response == null) {
                Messages.showMessageDialog(
                    project, "Empty response", "Error",
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