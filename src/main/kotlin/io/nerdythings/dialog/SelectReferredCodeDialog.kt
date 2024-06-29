package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBUI
import com.intellij.ui.components.JBScrollPane
import io.nerdythings.preferences.AppSettingsState
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import java.io.File

class SelectReferredCodeDialog(private var prompt: String, private val dialogTitle: String,
                               private var doUpdateSettingsPrompt: Boolean = false) : DialogWrapper(true) {

    private val contentPane: JPanel by lazy { JPanel() }
    private lateinit var radioButton4: JRadioButton
    private var additionalFiles: List<File> = listOf()

    init {
        init()
        this.title = dialogTitle
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
        val label = JLabel("Enter $dialogTitle request to chatGPT")
        val textArea = JTextArea(5, 100).apply {
            margin = JBUI.insets(10)
            text = prompt
            lineWrap = true
            wrapStyleWord = true
        }

        textArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updatePrompt(textArea.text)
            }

            override fun removeUpdate(e: DocumentEvent?) {
                updatePrompt(textArea.text)
            }

            override fun changedUpdate(e: DocumentEvent?) {
                updatePrompt(textArea.text)
            }
        })

        val scrollPane = JBScrollPane(textArea)

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

    private fun updatePrompt(newPrompt: String) {
        prompt = newPrompt
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

        radioButton4 = JRadioButton(
            "Send full code of current file and files to be selected in coming dialog",
            settings.shouldSendCode() == AppSettingsState.SendCodeMethod.SEND_FILE_AND_OTHERS,
        )

        listOf(radioButton1, radioButton2, radioButton3, radioButton4).forEach { radioButton ->
            radioButton.addItemListener { e ->
                if (e.stateChange == ItemEvent.SELECTED) {
                    settings.setShouldSendCode(
                        when (radioButton) {
                            radioButton1 -> AppSettingsState.SendCodeMethod.DONT_SEND
                            radioButton2 -> AppSettingsState.SendCodeMethod.SEND_A_FILE
                            radioButton3 -> AppSettingsState.SendCodeMethod.SEND_SELECTED_ONLY
                            radioButton4 -> AppSettingsState.SendCodeMethod.SEND_FILE_AND_OTHERS
                            else -> throw IllegalArgumentException("Unknown radio button")
                        }
                    )
                }
            }
        }

        val group = ButtonGroup().apply {
            add(radioButton1)
            add(radioButton2)
            add(radioButton3)
            add(radioButton4)
        }

        panel.add(radioButton1)
        panel.add(radioButton2)
        panel.add(radioButton3)
        panel.add(radioButton4)
    }

    override fun doOKAction() {
        if (radioButton4.isSelected) {
            val selectFilesDialog = SelectFilesDialog()
            if (selectFilesDialog.showAndGet()) {
                additionalFiles = selectFilesDialog.getSelectedFiles()
                AppSettingsState.instance.additionalFiles = additionalFiles.map { it.path }
            } else {
                JOptionPane.showMessageDialog(contentPane, "No extra files selected to send with question")
                return
            }
        }
        if (doUpdateSettingsPrompt) {
            AppSettingsState.instance.gptAsk = prompt  // Save the updated prompt
        }
        super.doOKAction()
    }

    fun getUpdatedPrompt(): String {
        return prompt
    }
}
