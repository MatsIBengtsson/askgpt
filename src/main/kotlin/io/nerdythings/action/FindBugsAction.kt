package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class FindBugsAction : CommonRequestReferralsAndResponseHandling("Find Bugs",
    AppSettingsState.instance.checkBugsQuestion)
    private const val progressText = "GPT is finding bugs in your code..."
    private const val noResponseText = "No response from GPT when asked to find bugs in your code"
