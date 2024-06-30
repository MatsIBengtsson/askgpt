package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class GenerateDocsAction : CommonRequestReferralsAndResponseHandling("Generate Docs"){
    private val progressText = "GPT is generating documentation for your code..."
    private val noResponseText = "No response from GPT when asked to generate documentation for your code"
    init {
        initialize(AppSettingsState.instance.writeDocsQuestion)
    }
}
