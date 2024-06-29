package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class RefactorCodeAction : CommonRequestReferralsAndResponseHandling("Refactor Code",
    AppSettingsState.instance.doRefactorPrompt)