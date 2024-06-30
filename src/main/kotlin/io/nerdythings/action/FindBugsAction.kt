package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class FindBugsAction : CommonRequestReferralsAndResponseHandling("Find Possible Issues",
    AppSettingsState.instance.checkBugsQuestion)
    private const val progressText = "GPT is finding possible issues in your code..."
    private const val noResponseText = "No response from GPT when asked to find possible issues in your code"
