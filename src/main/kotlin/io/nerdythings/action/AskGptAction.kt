package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

class AskGptAction : CommonRequestReferralsAndResponseHandling("Ask GPT",
    AppSettingsState.instance.gptAsk, doUpdateSettingsPrompt=true)