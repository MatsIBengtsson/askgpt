package io.nerdythings.startup

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class PluginStartup : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            // Your startup code here
            println("Application started up!")
        }
    }
}