package io.nerdythings.utils

import java.awt.Desktop
import java.net.URI

@Suppress("Unused")
object SystemUtil {

    fun openUrlInBrowser(url: String?) {
        if (url != null) {
            try {
                Desktop.getDesktop().browse(URI.create(url))
            } catch (_: IllegalArgumentException) {

            }
        }
    }
}