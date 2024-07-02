/* This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
*/
package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

class AskGptAction : CommonRequestReferralsAndResponseHandling("Ask GPT", doUpdateSettingsPrompt=true) {

    init {
        initialize(AppSettingsState.instance.gptAsk)
    }
}
