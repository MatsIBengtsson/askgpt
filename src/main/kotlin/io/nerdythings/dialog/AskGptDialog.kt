package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import io.nerdythings.preferences.AppSettingsState
import java.awt.GridLayout
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentListener


class AskGptDialog : DialogWrapper(true) {

    private val contentPane: JPanel by lazy {
        JPanel()
    }

    init {
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel().apply {
            setLayout(GridLayout(6, 20))
        }
        val label = JLabel("Enter question to chatGPT")
        val settings = AppSettingsState.instance
        val textArea = JTextArea(5, 100)
        textArea.margin = JBUI.insets(10)
        textArea.text = settings.gptAsk
        textArea.lineWrap = true
        textArea.wrapStyleWord = true

        val scrollPane = JBScrollPane(textArea)

        textArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                settings.gptAsk = textArea.text
            }

            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                settings.gptAsk = textArea.text
            }

            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                settings.gptAsk = textArea.text
            }
        })

        val checkBox = JCheckBox("Send file content with a question")
        checkBox.isSelected = settings.sendCodeWithGptAsk
        checkBox.addItemListener { e ->
            settings.sendCodeWithGptAsk = e.stateChange == ItemEvent.SELECTED
        }

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(Box.createVerticalStrut(20))
        panel.add(label)
        panel.add(Box.createVerticalStrut(10))
        panel.add(scrollPane)
        panel.add(Box.createVerticalStrut(10))
        panel.add(checkBox)
        panel.add(Box.createVerticalStrut(50))
        contentPane.add(panel)
        return contentPane
    }

}