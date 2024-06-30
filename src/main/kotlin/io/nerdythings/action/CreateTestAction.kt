package io.nerdythings.action

import io.nerdythings.preferences.AppSettingsState

internal class CreateTestAction : CommonRequestReferralsAndResponseHandling("Create Tests",
    AppSettingsState.instance.createTestQuestion)
    private const val progressText = "GPT is creating tests for your code..."
    private const val noResponseText = "No response from GPT when asked to create tests for your code"