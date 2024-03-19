package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import io.nerdythings.preferences.AppSettingsState
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Toolkit
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

            addLabeledTextField(this, "ChatGPT token", AppSettingsState.instance.gptToken.orEmpty()) {
                AppSettingsState.instance.gptToken = it
            }
            addLabeledTextField(this, "ChatGPT model", AppSettingsState.instance.gptModel) {
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
        onChange: (String) -> Unit
    ) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val halfScreenWidth = screenSize.width / 2

        val panel = JPanel().apply {
            setLayout(GridLayout(6, 20))
        }
        val label = JLabel(labelText)

        val textArea = JTextArea(5, 20)
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
        val preferredHeight = 150
        panel.preferredSize = Dimension(halfScreenWidth, preferredHeight)
        panel.add(Box.createVerticalStrut(10))
        panel.add(label)
        panel.add(Box.createVerticalStrut(10))
        panel.add(scrollPane)
        panel.add(Box.createVerticalStrut(50))
        parentPanel.add(panel)
    }
}