/* This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
*/
package io.nerdythings.action

import com.intellij.openapi.actionSystem.AnActionEvent
import io.nerdythings.dialog.SettingsDialog
import org.jetbrains.annotations.NotNull

class PluginSettingsAction :  IconVisibleAndEnabledAction() {

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        SettingsDialog().show()
    }
}
