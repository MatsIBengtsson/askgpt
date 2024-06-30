package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class RefactorCodeAction : CommonRequestReferralsAndResponseHandling("Refactor Code"){
    init {
        initialize(AppSettingsState.instance.doRefactorPrompt)
    }
}
