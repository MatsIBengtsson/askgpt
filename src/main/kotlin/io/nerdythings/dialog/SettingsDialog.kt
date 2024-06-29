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

class SettingsDialog : DialogWrapper(true) {
    private var contentPane: JPanel? = null
    private var finalUserQuestion: String? = null

    init {
        init()
    }

    override fun createCenterPanel(): JComponent? {
        contentPane = JPanel().apply {
            layout = GridLayout(6, 1)
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