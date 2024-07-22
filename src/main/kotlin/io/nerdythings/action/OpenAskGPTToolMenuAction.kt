package io.nerdythings.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPopupMenu
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import io.nerdythings.utils.UserResponseUtil
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle
import javax.swing.SwingUtilities


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
        val editorComponent = editor?.contentComponent
        if (editorComponent != null) {
            // Convert point from screen if we have a valid editor component
            SwingUtilities.convertPointFromScreen(pointerLocation, editorComponent)
            val visibleRect: Rectangle = editorComponent.visibleRect
            displayPopUp(popupMenu, pointerLocation, Pair(visibleRect.width, visibleRect.height), editorComponent)
        } else {
            // Fallback case: Ensure popup does not arbitrarily float
            val screenSize = java.awt.Toolkit.getDefaultToolkit().screenSize
            displayPopUp(popupMenu, pointerLocation, Pair(screenSize.width, screenSize.height))
        }
    }

    private fun displayPopUp(popupMenu: ActionPopupMenu, pointerLocation: Point, wantedLocation: Pair<Int, Int>,
                             invoker: javax.swing.JComponent?=null) {
        val (width, height) = wantedLocation
        val (x, y) = calculatePopupLocation(popupMenu, pointerLocation, Pair(width, height))
        popupMenu.component.show(invoker, x, y)
    }

    private fun calculatePopupLocation(popupMenu: ActionPopupMenu, pointerLocation: Point,
                                       wantedLocation: Pair<Int, Int>): Pair<Int, Int> {
        val (width, height) = wantedLocation
        val x = pointerLocation.x.coerceIn(0, width - popupMenu.component.preferredSize.width)
        val y = pointerLocation.y.coerceIn(0, height - popupMenu.component.preferredSize.height)
        return Pair(x, y)
    }
}
