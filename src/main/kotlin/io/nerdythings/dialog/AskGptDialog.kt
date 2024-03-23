package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import io.nerdythings.preferences.AppSettingsState
import java.awt.GridLayout
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentListener


class AskGptDialog() : DialogWrapper(true) {

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

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(Box.createVerticalStrut(20))
        panel.add(label)
        panel.add(Box.createVerticalStrut(10))
        panel.add(scrollPane)
        panel.add(Box.createVerticalStrut(10))
        addRadioGroup(panel)
        panel.add(Box.createVerticalStrut(50))

        contentPane.add(panel)
        return contentPane
    }

    private fun addRadioGroup(panel: JPanel) {
        val settings = AppSettingsState.instance

        val radioButton1 = JRadioButton(
            "Send just the question",
            settings.shouldSendCode() == AppSettingsState.SendCodeMethod.DONT_SEND,
        )

        val radioButton2 = JRadioButton(
            "Send full code of the file",
            settings.shouldSendCode() == AppSettingsState.SendCodeMethod.SEND_A_FILE,
        )

        val radioButton3 = JRadioButton(
            "Send selected text only (if selected)",
            settings.shouldSendCode() == AppSettingsState.SendCodeMethod.SEND_SELECTED_ONLY,
        )

        radioButton1.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                settings.setShouldSendCode(AppSettingsState.SendCodeMethod.DONT_SEND)
            }
        }
        radioButton2.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                settings.setShouldSendCode(AppSettingsState.SendCodeMethod.SEND_A_FILE)
            }
        }
        radioButton3.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                settings.setShouldSendCode(AppSettingsState.SendCodeMethod.SEND_SELECTED_ONLY)
            }
        }

        val group = ButtonGroup()

        group.add(radioButton1)
        group.add(radioButton2)
        group.add(radioButton3)

        panel.add(radioButton1)
        panel.add(radioButton2)
        panel.add(radioButton3)
    }

}