package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class FindBugsAction : CommonRequestReferralsAndResponseHandling("Find Possible Issues") {
    private val progressText = "GPT is finding possible issues in your code..."
    private val noResponseText = "No response from GPT when asked to find possible issues in your code"
    init {
        initialize(AppSettingsState.instance.checkBugsQuestion)
    }
}
