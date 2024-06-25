package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import io.nerdythings.preferences.AppSettingsState
import java.awt.GridLayout
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentListener
import java.io.File

class AskGptDialog : DialogWrapper(true) {

    private val contentPane: JPanel by lazy {
        JPanel()
    }

    private lateinit var radioButton4: JRadioButton
    private val additionalFiles: MutableList<File> = mutableListOf()  // List to keep track of selected files
    private val addedFilesPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }

    init {
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel().apply {
            layout = GridLayout(6, 20)
        }
        val label = JLabel("Enter request to chatGPT")
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

        radioButton4 = JRadioButton(
            "Send full code of current file and files to be selected in coming dialog",
            settings.shouldSendCode() == AppSettingsState.SendCodeMethod.SEND_FILE_AND_OTHERS,
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
        radioButton4.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                settings.setShouldSendCode(AppSettingsState.SendCodeMethod.SEND_FILE_AND_OTHERS)
            }
        }

        val group = ButtonGroup()

        group.add(radioButton1)
        group.add(radioButton2)
        group.add(radioButton3)
        group.add(radioButton4)

        panel.add(radioButton1)
        panel.add(radioButton2)
        panel.add(radioButton3)
        panel.add(radioButton4)
    }

    override fun doOKAction() {
        val settings = AppSettingsState.instance
        if (radioButton4.isSelected) {
            additionalFiles.clear()
            selectAdditionalFiles()
        }
        super.doOKAction()
    }

    private fun selectAdditionalFiles() {
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        val projectBasePath = project?.basePath ?: System.getProperty("user.home")  // Fallback to home directory
        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = true
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDirectory = File(projectBasePath)
        }

        fileChooser.selectedFiles = additionalFiles.toTypedArray()  // Preselect already chosen files

        val option = fileChooser.showOpenDialog(contentPane)
        if (option == JFileChooser.APPROVE_OPTION) {
            val files = fileChooser.selectedFiles
            additionalFiles.addAll(files)
            val settings = AppSettingsState.instance
            settings.additionalFiles = additionalFiles.map { it.path }
        }
    }
}