/* This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
*/
package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import io.nerdythings.preferences.AppSettingsState
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*
import javax.swing.event.DocumentListener
import javax.swing.text.JTextComponent
import io.nerdythings.keybinding.KeyBindingConfigurator
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class SettingsDialog : DialogWrapper(true) {
    private lateinit var contentPane: JPanel
    private var finalUserQuestion: String? = null
    private lateinit var keyBindingField: JTextField

    init {
        init()
    }

    override fun createCenterPanel(): JComponent {
        contentPane = JPanel().apply {
            layout = GridLayout(7, 1)
            createTextLink(
                this,
                "ChatGPT token (click here to create)",
                "https://platform.openai.com/api-keys",
                AppSettingsState.instance.gptToken.orEmpty(),
            ) {
                AppSettingsState.instance.gptToken = it
            }
            createTextLink(
                this,
                "ChatGPT model (click here to choose)",
                "https://platform.openai.com/account/limits",
                AppSettingsState.instance.gptModel,
            ) {
                AppSettingsState.instance.gptModel = it
            }
            addKeyBindingField(this, "AskGPT Menu Key Binding. Specify by pressing wanted key combination or leave value in field unchanged",
                AppSettingsState.instance.askGptMenuKeyBinding ?: "") {
                AppSettingsState.instance.askGptMenuKeyBinding = it.ifEmpty { null }
                if (it.isEmpty()) {
                    clearKeyBindingField()
                }
                if (!KeyBindingConfigurator.configureKeyBindings()) {
                    clearKeyBindingField()
                }
            }
            addLabeledTextField(this, "ChatGPT Default Question", AppSettingsState.instance.gptAsk, textRows = 1) {
                finalUserQuestion = it
            }
            addLabeledTextField(this, "ChatGpt Refactor Code Prompt", AppSettingsState.instance.doRefactorPrompt) {
                finalUserQuestion = it
            }
            addLabeledTextField(this, "ChatGpt Test Question", AppSettingsState.instance.createTestQuestion) {
                finalUserQuestion = it
            }
            addLabeledTextField(this, "ChatGpt Bugs Question", AppSettingsState.instance.checkBugsQuestion) {
                finalUserQuestion = it
            }
            addLabeledTextField(this, "ChatGPT Docs Question", AppSettingsState.instance.writeDocsQuestion) {
                finalUserQuestion = it
            }
        }

        return contentPane
    }

    private fun addKeyBindingField(
        parentPanel: JPanel,
        labelText: String,
        initialBinding: String,
        onChange: (String) -> Unit
    ) {
        val label = JLabel(labelText)
        keyBindingField = JTextField(initialBinding).apply {
            addKeyListener(object : KeyAdapter() {
                private var keyCombination: String = initialBinding

                override fun keyPressed(e: KeyEvent) {
                    e.source as JTextField
                    keyCombination = getKeyCombination(e)
                    text = keyCombination
                }

                override fun keyReleased(e: KeyEvent) {
                    onChange(text)
                }
            })
        }
        val panel = createPanelWithComponents(label, keyBindingField)
        parentPanel.add(panel)
    }

    private fun getKeyCombination(e: KeyEvent): String {
        val isKeyCode = e.keyChar == KeyEvent.CHAR_UNDEFINED
        val modifiers = KeyEvent.getModifiersExText(e.modifiersEx)
        val keyText = KeyEvent.getKeyText(e.keyCode)
        return if (isKeyCode) {
            composeKeyCombination(modifiers, keyText)
        } else {
            val keyCombination = composeKeyCombination(modifiers, keyText)
            if (e.keyCode == KeyEvent.VK_ENTER && modifiers.isNotEmpty()) {
                e.consume()  // Consume the ENTER key if it's part of a combination
            }
            keyCombination
        }
    }

    private fun composeKeyCombination(modifiers: String, keyText: String): String {
        return if (modifiers.isNotEmpty()) {
            if (modifiers == keyText) {
                    modifiers.lowercase()
            } else {
                    "${modifiers.lowercase()} ${keyText.uppercase()}"
            }
        } else {
                keyText.uppercase()
        }
    }

    private fun clearKeyBindingField() {
        if (::keyBindingField.isInitialized) {
            keyBindingField.text = ""
        }
    }

    private fun addTextFieldListener(textField: JTextComponent, onChange: (String) -> Unit) {
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                onChange.invoke(textField.text)
            }

            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                onChange.invoke(textField.text)
            }

            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                onChange.invoke(textField.text)
            }
        })
    }

    private fun createPanelWithComponents(vararg components: JComponent): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            components.forEach {
                add(Box.createVerticalStrut(5))
                add(it)
            }
            add(Box.createVerticalStrut(20))
        }
    }

    private fun addLabeledTextField(
        parentPanel: JPanel,
        labelText: String,
        textFieldText: String,
        textRows: Int = 6,
        onChange: (String) -> Unit
    ) {
        val label = JLabel(labelText)
        val textArea = JTextArea(textRows, 100).apply {
            margin = JBUI.insets(10)
            text = textFieldText
            lineWrap = true
            wrapStyleWord = true
        }
        val scrollPane = JBScrollPane(textArea)
        addTextFieldListener(textArea, onChange)

        val panel = createPanelWithComponents(label, scrollPane)
        parentPanel.add(panel)
    }

    private fun createTextLink(
        parentPanel: JPanel,
        labelText: String,
        linkURL: String,
        textFieldText: String,
        onChange: (String) -> Unit
    ) {
        val textArea = JTextField(textFieldText, 100).apply {
            margin = JBUI.insets(10)
        }
        addTextFieldListener(textArea, onChange)

        val linkLabel = JLabel(labelText).apply {
            foreground = Color.BLUE.darker()
            cursor = Cursor(Cursor.HAND_CURSOR)
            toolTipText = linkURL
            text = "<html><a href=''>$labelText</a></html>"
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    try {
                        Desktop.getDesktop().browse(URI(linkURL))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            })
        }

        val panel = createPanelWithComponents(linkLabel, textArea)
        parentPanel.add(panel)
    }

    fun getUpdatedUserQuestion(): String? {
        return finalUserQuestion
    }
}