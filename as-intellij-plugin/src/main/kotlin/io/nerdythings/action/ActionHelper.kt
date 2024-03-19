package io.nerdythings.action

import io.nerdythings.model.ActionRequest
import io.nerdythings.model.ActionRequestBuilder
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.NotNull
import java.io.IOException

object ActionHelper {

    fun parseEvent(@NotNull event: AnActionEvent): ActionRequest {
        val currentProject: Project? = event.project
        val builder = ActionRequestBuilder(projectName = currentProject?.name)
        builder.presentationName = event.presentation.text
        builder.presentationDescription = event.presentation.description
        // If an element is selected in the editor, add info about it.
        val nav: Navigatable? = event.getData(CommonDataKeys.NAVIGATABLE)
        if (nav != null) {
            builder.elementType = nav.toString()
        }

        val element: PsiElement? = event.getData(CommonDataKeys.PSI_ELEMENT)
        if (element != null) {
            builder.elementContext = element.context.toString()
            builder.language = element.language.toString()
        }

        // Get the VirtualFile object for the selected file
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        // Check if the file is not null and is a regular file
        if (file != null && file.exists() && !file.isDirectory) {
            // Get the contents of the file as a string
            builder.localFilePath = file.path
            try {
                val content = String(file.contentsToByteArray(), file.charset)
                builder.fileContent = content
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return builder.build()
    }
}