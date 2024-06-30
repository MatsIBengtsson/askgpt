package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class GenerateDocsAction : CommonRequestReferralsAndResponseHandling("Generate Docs",
    AppSettingsState.instance.writeDocsQuestion)
    private const val progressText = "GPT is generating documentation for your code..."
    private const val noResponseText = "No response from GPT when asked to generate documentation for your code"
