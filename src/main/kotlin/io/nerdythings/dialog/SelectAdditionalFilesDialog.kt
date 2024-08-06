package io.nerdythings.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.project.ProjectManager
import java.io.File
import javax.swing.*

class SelectFilesDialog(initialFile: File? = null) : DialogWrapper(true) {

    companion object {
        var lastSelectedDirectory: File? = null
    }

    private val contentPane: JPanel by lazy {
        JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
    }
    private val selectedFilesLabel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
    private val selectedFiles: MutableList<File> = mutableListOf()

    init {
        initialFile?.let {
            selectedFiles.add(it)
            updateSelectedFilesLabel()
        }
        init()
        title = "Select Additional Files"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }

        val selectFilesButton = JButton("Select files to Add").apply {
            addActionListener { selectAdditionalFiles() }
        }

        val clearFilesButton = JButton("Clear so far selected files").apply {
            addActionListener {
                selectedFiles.clear()
                selectedFilesLabel.removeAll()
                selectedFilesLabel.revalidate()
                selectedFilesLabel.repaint()
            }
        }

        val selectedFilesTitle = JLabel("So Far Selected Files list:")
        val separator = JSeparator(SwingConstants.HORIZONTAL)

        panel.add(selectFilesButton)
        panel.add(Box.createVerticalStrut(5))
        panel.add(clearFilesButton)
        panel.add(Box.createVerticalStrut(10))
        panel.add(separator)
        panel.add(Box.createVerticalStrut(10))
        panel.add(selectedFilesTitle)
        panel.add(selectedFilesLabel)

        contentPane.add(panel)
        return contentPane
    }

    private fun selectAdditionalFiles() {
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        val projectBasePath = project?.basePath ?: System.getProperty("user.home")
        val defaultDirectory = lastSelectedDirectory ?: File(projectBasePath)

        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = true
            currentDirectory = if (defaultDirectory.exists() && defaultDirectory.isDirectory) defaultDirectory else File(projectBasePath)
        }

        val option = fileChooser.showOpenDialog(contentPane)
        if (option == JFileChooser.APPROVE_OPTION) {
            val files = fileChooser.selectedFiles
            selectedFiles.addAll(files)
            updateSelectedFilesLabel()

            // Update last selected directory
            lastSelectedDirectory = fileChooser.currentDirectory
        }
    }

    private fun updateSelectedFilesLabel() {
        selectedFilesLabel.removeAll()
        selectedFiles.forEach { file ->
            selectedFilesLabel.add(JLabel(file.absolutePath))
        }
        selectedFilesLabel.revalidate()
        selectedFilesLabel.repaint()
    }

    fun getSelectedFiles(): List<File> {
        return selectedFiles
    }
}