package io.nerdythings.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import io.nerdythings.action.ActionHelper
import java.io.File

object FileUtil {

    fun appendFilesAsContentAndAddFilesToList(event: AnActionEvent, questionText: StringBuilder, filesToSend: MutableList<File>) {
        questionText.append("\nFiles with code:\n")
        val parsedEvent = ActionHelper.parseEvent(event)
        val localFilePath = parsedEvent.localFilePath
        if (localFilePath != null) {
            filesToSend.add(File(localFilePath))
        }
    }

    fun appendFilesInListToQuestion(questionText: StringBuilder, filesToSend: List<File>) {
        filesToSend.forEach { file ->
            questionText.append("filename: ${file.name}\n")
            questionText.append(file.readText())
            questionText.append("\n")
        }
    }
}