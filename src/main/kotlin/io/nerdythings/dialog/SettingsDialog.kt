package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import io.nerdythings.preferences.AppSettingsState
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentListener


class SettingsDialog : DialogWrapper(true) {
    private var contentPane: JPanel? = null

    init {
        init()
    }

    override fun createCenterPanel(): JComponent? {
        contentPane = JPanel().apply {
            setLayout(GridLayout(5, 1))

            addLabeledTextField(this, "ChatGPT token", AppSettingsState.instance.gptToken.orEmpty(), textRows = 1) {
                AppSettingsState.instance.gptToken = it
            }
            addLabeledTextField(this, "ChatGPT model", AppSettingsState.instance.gptModel, textRows = 1) {
                AppSettingsState.instance.gptModel = it
            }
            addLabeledTextField(this, "ChatGpt Test Question", AppSettingsState.instance.createTestQuestion) {
                AppSettingsState.instance.createTestQuestion = it
            }
            addLabeledTextField(this, "ChatGpt Bugs Question", AppSettingsState.instance.checkBugsQuestion) {
                AppSettingsState.instance.checkBugsQuestion = it
            }
            addLabeledTextField(this, "ChatGPT Docs Question", AppSettingsState.instance.writeDocsQuestion) {
                AppSettingsState.instance.writeDocsQuestion = it
            }
        }

        return contentPane
    }

    private fun addLabeledTextField(
        parentPanel: JPanel,
        labelText: String,
        textFieldText: String,
        textRows: Int = 6,
        onChange: (String) -> Unit
    ) {
        val panel = JPanel().apply {
            setLayout(GridLayout(textRows + 1, 1))
        }
        val label = JLabel(labelText)

        val textArea = JTextArea(textRows, 100)
        textArea.margin = JBUI.insets(10)
        textArea.text = textFieldText
        textArea.lineWrap = true
        textArea.wrapStyleWord = true

        val scrollPane = JBScrollPane(textArea)

        textArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                onChange.invoke(textArea.text)
            }

            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                onChange.invoke(textArea.text)
            }

            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                onChange.invoke(textArea.text)
            }
        })

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(Box.createVerticalStrut(5))
        panel.add(label)
        panel.add(Box.createVerticalStrut(5))
        panel.add(scrollPane)
        panel.add(Box.createVerticalStrut(20))
        parentPanel.add(panel)
    }
}