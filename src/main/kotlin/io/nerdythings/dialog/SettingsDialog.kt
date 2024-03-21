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


class SettingsDialog : DialogWrapper(true) {
    private var contentPane: JPanel? = null

    init {
        init()
    }

    override fun createCenterPanel(): JComponent? {
        contentPane = JPanel().apply {
            setLayout(GridLayout(6, 1))
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
                AppSettingsState.instance.gptAsk = it
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

    private fun createTextLink(
        parentPanel: JPanel,
        labelText: String,
        linkURL: String,
        textFieldText: String,
        onChange: (String) -> Unit
    ) {
        val panel = JPanel().apply {
            setLayout(GridLayout(2, 1))
        }

        val textArea = JTextField(textFieldText, 100)

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

        textArea.margin = JBUI.insets(10)
        val linkLabel = JLabel(labelText)
        linkLabel.foreground = Color.BLUE.darker()
        linkLabel.cursor = Cursor(Cursor.HAND_CURSOR)
        linkLabel.toolTipText = linkURL
        linkLabel.text = "<html><a href=''>" + linkLabel.text + "</a></html>"

        linkLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                try {
                    Desktop.getDesktop().browse(URI(linkURL))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })
        panel.add(linkLabel)
        panel.add(textArea)
        parentPanel.add(panel)
    }
}