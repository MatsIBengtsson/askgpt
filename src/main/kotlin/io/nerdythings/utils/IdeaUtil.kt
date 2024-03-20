package io.nerdythings.utils

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


object IdeaUtil {

    fun writeAndOpenFile(project: Project, path: String, classText: String) {
        writeAndOpenFile(project, File(path), classText)
    }

    fun replaceFileContent(project: Project, editor: Editor, text: String) {
        val document = editor.document
        WriteCommandAction.runWriteCommandAction(project) {
            document.setText(text)
        }
    }

    fun insertIntoSameFile(project: Project, editor: Editor, text: String) {
        val document: Document = editor.document
        val caretModel: CaretModel = editor.caretModel
        val offset = caretModel.offset
        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(offset, text)
        }
    }

    private fun writeAndOpenFile(project: Project, file: File, classText: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            file.createNewFile()
            var writer: BufferedWriter? = null
            try {
                writer = BufferedWriter(FileWriter(file))
                writer.write(classText)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    writer?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)?.let { vFile ->
                val psiFile = PsiManager.getInstance(project).findFile(vFile)
                psiFile?.let {
                    CodeStyleManager.getInstance(project).reformatText(psiFile, 0, psiFile.textLength)
                    psiFile.navigate(true)
                }
            }
        }
    }
}