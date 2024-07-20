package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import io.nerdythings.utils.UserResponseUtil
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.SwingUtilities
import java.awt.event.InputEvent

class OpenAskGPTToolMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // Retrieve the 'AskGPTToolMenu' action group and display it if it exists
        val actionManager = ActionManager.getInstance()
        val askGPTToolMenuGroup = actionManager.getAction("AskGPTToolMenu") as? ActionGroup
        if (askGPTToolMenuGroup != null) {
            showMenu(e, askGPTToolMenuGroup)
        } else {
            Messages.showErrorDialog("AskGPT Tool Menu not found", "Error")
        }
    }

    private fun showMenu(e: AnActionEvent, actionGroup: ActionGroup) {
        // Ensure we are in an editor window
        val project = e.project
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        if (!UserResponseUtil.validateInputUsable(project, editor)) {
            return
        }
        // Create and show the popup menu
        val actionManager = ActionManager.getInstance()
        val popupMenu = actionManager.createActionPopupMenu("AskGPT", actionGroup)

        // Determine cursor location to show popup menu
        val pointerLocation: Point = MouseInfo.getPointerInfo().location
        val inputEvent = e.inputEvent
        val editorComponent = editor?.contentComponent

        if (editorComponent != null) {
            // Convert point from screen if we have a valid editor component
            SwingUtilities.convertPointFromScreen(pointerLocation, editorComponent)

                // Properly positioning the popup menu near the cursor
            val visibleRect: Rectangle = editorComponent.visibleRect
                val x = pointerLocation.x.coerceIn(0, visibleRect.width - popupMenu.component.preferredSize.width)
                val y = pointerLocation.y.coerceIn(0, visibleRect.height - popupMenu.component.preferredSize.height)
            popupMenu.component.show(editorComponent, x, y)
            } else {
            // Fallback case: Ensure popup does not arbitrarily float
            // Obtain screen dimensions to prevent popup from going off-screen
            val screenSize = java.awt.Toolkit.getDefaultToolkit().screenSize
            val x = pointerLocation.x.coerceIn(0, screenSize.width - popupMenu.component.preferredSize.width)
            val y = pointerLocation.y.coerceIn(0, screenSize.height - popupMenu.component.preferredSize.height)
            popupMenu.component.show(null, x, y)
        }
    }
}
