package io.nerdythings.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.NotNull

class OpenNerdyThingsAction : IconVisibleAndEnabledAction() {

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        BrowserUtil.open("https://youtube.com/@Nerdy.Things?si=ePGW7vya2NR5Ugei")
    }
}