package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class CreateTestAction : CommonRequestReferralsAndResponseHandling("Create Tests") {
    private val progressText = "GPT is creating tests for your code..."
    private val noResponseText = "No response from GPT when asked to create tests for your code"

    init {
        initialize(AppSettingsState.instance.createTestQuestion)
    }
}
