package io.nerdythings.preferences

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "io.nerdythings.preferences.AppSettingsState", storages = [Storage("NerdyAskAiSettingsPlugin.xml")])
internal class AppSettingsState : PersistentStateComponent<AppSettingsState> {

    var gptToken: String?= null
    var gptModel: String  = "gpt-4"
    var gptAsk: String  = "What is the purpose of humankind?"
    var sendCodeWithGptAsk: Boolean  = false
    var createTestQuestion: String =  "Could you be so kind and create tests for this file? " +
            "Please, make sure that coverage is at least 80% and tests have success and failure scenario. " +
            "Also, your answer should have only code .  " +
            "No intro our outro words and don't cut the code in the response. It should be fully written. " +
            " Thank you!"

    var checkBugsQuestion: String = "Could you be so kind and check this file? " +
            "Please, find all bugs, null pointers, memory leaks and improvements, that improve this code. " +
            "Also, your answer should have only code and comments inside the code, above lines that it was meant for.  " +
            "No intro our outro words and don't cut the code in the response. It should be fully written. " +
            " Thank you!"

    var writeDocsQuestion: String = "Could you be so kind and write documentation and comments for this file? " +
            "Please, cover all methods and important or complicated logic, but do not change the code. " +
            "Do not cover obvious parts, like variables with proper names. " +
            "Also, your answer should have only code with documentation and comments. " +
            "No intro our outro words and don't cut the code in the response. It should be fully written. " +
            " Thank you!"

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }
}