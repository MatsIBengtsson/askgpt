package io.nerdythings.action

import io.nerdythings.model.ActionRequest
import io.nerdythings.model.ActionRequestBuilder
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import io.nerdythings.utils.FileUtil
import org.jetbrains.annotations.NotNull

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

        val localFilePath = FileUtil.determineLocalFilePath(event)
        if (localFilePath != null) {
            // Get the contents of the file as a string
            builder.localFilePath = localFilePath
            val content = FileUtil.getVirtualFileContent(event)
            builder.fileContent = content
        }
        return builder.build()
    }
}
