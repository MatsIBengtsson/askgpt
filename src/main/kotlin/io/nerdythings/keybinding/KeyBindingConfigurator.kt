package io.nerdythings.keybinding

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.keymap.ex.KeymapManagerEx
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.ui.Messages
import io.nerdythings.preferences.AppSettingsState
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class KeyBindingConfigurator {

    companion object {
        private const val ASK_GPT_MENU_ACTION_ID = "io.nerdythings.action.OpenAskGPMIBAddMenuAction"

        fun configureKeyBindings(): Boolean {
            val settings = AppSettingsState.instance
            val keymapManager = KeymapManagerEx.getInstanceEx()
            val activeKeymap = keymapManager.activeKeymap

            settings.askGptMenuKeyBinding?.let { keyBinding ->
                val keyStroke = parseKeyBinding(keyBinding)
                if (keyStroke != null) {
                    // Check if the keystroke is already assigned.
                    val actionsAssigned = activeKeymap.getActionIds(keyStroke)
//                    if (actionsAssigned.isNotEmpty()) {
//                        Messages.showErrorDialog(
//                            "The keystroke $keyBinding is already assigned to: ${actionsAssigned.joinToString(", ")}",
//                            "Key Binding Conflict"
//                        )
//                        return false
//                    }
                    return executeCommandHandlingExceptions(activeKeymap, keyStroke)
                } else {
                    settings.askGptMenuKeyBinding = null
                    return false
                }
            } ?: run {
                activeKeymap.removeAllActionShortcuts(ASK_GPT_MENU_ACTION_ID)
                return true
            }
        }

        private fun parseKeyBinding(keyBinding: String): KeyStroke? {
            var keyStroke = KeyStroke.getKeyStroke(keyBinding)
            if (keyStroke != null) {
                return keyStroke
            }
            keyStroke = KeyStroke.getKeyStroke(keyBinding.replace("+", " "))
            if (keyStroke != null) {
                return keyStroke
            }
            keyStroke = KeyStroke.getKeyStroke(keyBinding.replace(" ", "+"))
            if (keyStroke != null) {
                return keyStroke
            }
            // val ks2 = KeyStroke.getKeyStroke("shift pressed R")
            // java.awt.AWTKeyStroke.getAWTKeyStroke("ctrl pressed ENTER")
            // val ks3 = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.SHIFT_DOWN_MASK)
            val parts = keyBinding.split(" ")
            val keyText = parts.lastOrNull() ?: return null
            val upperKeyText = keyText.uppercase()
            val modifiersText = parts.dropLast(1).joinToString(" ")
            val lowerModifiersText = modifiersText.lowercase()
            keyStroke = KeyStroke.getKeyStroke("$lowerModifiersText pressed $upperKeyText")
            if (keyStroke != null) {
                return keyStroke
            }
            val keyCode = handleKeyIncludingSpecialKeys(keyText)
            val modifiers = parseModifiers(modifiersText)
            return KeyStroke.getKeyStroke(keyCode, modifiers)
        }

        private fun handleKeyIncludingSpecialKeys(keyText: String): Int {
            val specialKeys = mapOf(
                "enter" to KeyEvent.VK_ENTER,
                "insert" to KeyEvent.VK_INSERT,
                "delete" to KeyEvent.VK_DELETE,
                "backspace" to KeyEvent.VK_BACK_SPACE,
                "escape" to KeyEvent.VK_ESCAPE,
                "tab" to KeyEvent.VK_TAB,
                "space" to KeyEvent.VK_SPACE,
                "up" to KeyEvent.VK_UP,
                "down" to KeyEvent.VK_DOWN,
                "left" to KeyEvent.VK_LEFT,
                "right" to KeyEvent.VK_RIGHT,
                "home" to KeyEvent.VK_HOME,
                "end" to KeyEvent.VK_END,
                "pageup" to KeyEvent.VK_PAGE_UP,
                "pagedown" to KeyEvent.VK_PAGE_DOWN
            )
            val specialKeyCode = specialKeys[keyText.lowercase()]
            if (specialKeyCode != null) {
                return specialKeyCode
            }
            val firstChar = keyText.lowercase().firstOrNull()
            return if (firstChar != null) {
                KeyEvent.getExtendedKeyCodeForChar(firstChar.code)
            } else {
                KeyEvent.VK_UNDEFINED
            }
        }

        private fun parseModifiers(modifiersText: String): Int {
            return modifiersText.split(" ").fold(0) { acc, mod ->
                acc or when (mod.lowercase()) {
                    "shift" -> KeyEvent.SHIFT_DOWN_MASK
                    "ctrl", "control" -> KeyEvent.CTRL_DOWN_MASK
                    "meta" -> KeyEvent.META_DOWN_MASK
                    "alt" -> KeyEvent.ALT_DOWN_MASK
                    else -> 0
                }
            }
        }

        private fun executeCommandHandlingExceptions(activeKeymap: Keymap, keyStroke: KeyStroke): Boolean {
            return try {
                CommandProcessor.getInstance().executeCommand(null, {
                    activeKeymap.removeAllActionShortcuts(ASK_GPT_MENU_ACTION_ID)
                    activeKeymap.addShortcut(ASK_GPT_MENU_ACTION_ID, KeyboardShortcut(keyStroke, null))
                }, "Configure Key Binding", null)
                true
            } catch (e: Exception) {
                Messages.showErrorDialog(e.message, "Error Configuring Key Binding")
                false
            }
        }
    }

    fun customizeKeys() {
        configureKeyBindings()
    }
}