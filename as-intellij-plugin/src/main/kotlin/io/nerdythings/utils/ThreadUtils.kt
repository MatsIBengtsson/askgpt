package io.nerdythings.utils

import com.intellij.openapi.application.ApplicationManager

fun runOnUiThread(function: () -> Unit) {
    ApplicationManager.getApplication().invokeLater {
        function.invoke()
    }
}