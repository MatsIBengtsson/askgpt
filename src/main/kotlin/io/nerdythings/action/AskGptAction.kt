package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

class AskGptAction : CommonRequestReferralsAndResponseHandling("Ask GPT", doUpdateSettingsPrompt=true) {

    init {
        initialize(AppSettingsState.instance.gptAsk)
    }
}
