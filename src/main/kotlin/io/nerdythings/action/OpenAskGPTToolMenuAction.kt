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
import java.awt.Toolkit
import javax.swing.JComponent
import javax.swing.SwingUtilities
import java.awt.Window


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
        val mousePointerLocation: Point = MouseInfo.getPointerInfo().location
        val editorComponent = editor?.contentComponent
        var convertedPointerLocation = Point(mousePointerLocation)
        val screenSize: Rectangle = getScreenSize()
        if (editorComponent != null) {
            // Convert point from screen if we have a valid editor component
            SwingUtilities.convertPointFromScreen(convertedPointerLocation, editorComponent)
            val visibleRect: Rectangle = editorComponent.visibleRect
            val (maxPopupX, maxPopupY) = Pair(visibleRect.x + screenSize.width, visibleRect.y + screenSize.height)
            val editorLocationOnScreen = editorComponent.locationOnScreen
            val adjustedPointerLocation = Point(
                mousePointerLocation.x - editorLocationOnScreen.x,
                mousePointerLocation.y - editorLocationOnScreen.y)
            val parentEditorWindow = SwingUtilities.getWindowAncestor(editorComponent)
            val (parentEditorWindowLocation, parentAdjustedPointerLocation) = getParentWindowPoints(parentEditorWindow, mousePointerLocation)
            val limitedPointerLocation = Point(
                convertedPointerLocation.x.coerceIn(visibleRect.x, visibleRect.x + visibleRect.width - popupMenu.component.preferredSize.width),
                convertedPointerLocation.y.coerceIn(visibleRect.y, visibleRect.y + visibleRect.height - popupMenu.component.preferredSize.height)
            )
            // displayMouseLocationPopUp(popupMenu, convertedPointerLocation, editorComponent)
            displayVisibleSizeAdjustedPopUp(popupMenu, convertedPointerLocation, Pair(maxPopupX, maxPopupY),
                editorComponent)
        } else {
            // Fallback case: Ensure popup does not arbitrarily float
            displayVisibleSizeAdjustedPopUp(popupMenu, mousePointerLocation, Pair(screenSize.width, screenSize.height), null)
        }
    }

    private fun getParentWindowPoints(parentEditorWindow: Window, mousePointerLocation: Point): Pair<Point, Point> {
        // Get the location of the editor window on screen
        val parentEditorWindowLocation = parentEditorWindow.locationOnScreen

        // Adjust the pointer location relative to the editor window
        val parentAdjustedPointerLocation = Point(
            mousePointerLocation.x - parentEditorWindowLocation.x,
            mousePointerLocation.y - parentEditorWindowLocation.y
        )
        return Pair(parentEditorWindowLocation, parentAdjustedPointerLocation)
    }

    private fun displayMouseLocationPopUp(popupMenu: ActionPopupMenu, pointerLocation: Point, invoker: JComponent?) {
        popupMenu.component.show(invoker, pointerLocation.x, pointerLocation.y)
    }

    private fun displayVisibleSizeAdjustedPopUp(popupMenu: ActionPopupMenu, pointerLocation: Point,
                                                acceptableLocation: Pair<Int, Int>, invoker: JComponent? = null) {
        val (width, height) = acceptableLocation
        val (x, y) = calculateSizeAdjustedPopupLocation(popupMenu, pointerLocation, Pair(width, height))
        popupMenu.component.show(invoker, x, y)
    }

    private fun calculateSizeAdjustedPopupLocation(popupMenu: ActionPopupMenu, pointerLocation: Point,
                                                   acceptableLocation: Pair<Int, Int>): Pair<Int, Int> {
        val (width, height) = acceptableLocation
        val x = pointerLocation.x.coerceIn(0, width - popupMenu.component.preferredSize.width)
        val y = pointerLocation.y.coerceIn(0, height - popupMenu.component.preferredSize.height)
        return Pair(x, y)
    }

    private fun getScreenSize(): Rectangle {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        return Rectangle(screenSize)
    }
}