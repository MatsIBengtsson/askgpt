/* This file has been modified by Mats Bengtsson.
Original file is part of the Nerdy Things AskGPT project.
*/
package io.nerdythings.preferences

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "io.nerdythings.preferences.AppSettingsState", storages = [Storage("NerdyAskAiSettingsPlugin.xml")])
internal class AppSettingsState : PersistentStateComponent<AppSettingsState> {

    var gptToken: String? = null
    var gptModel: String = "gpt-4o"
    var askGptMenuKeyBinding: String? = "Ctrl ENTER"
    var gptPrePrompt: String = "You need to read the whole text and all enclosed code before defining what should be answered. " +
            "You need to answer all questions asked. You need to consider all aspects of the problem stated. " +
            "You need to consider all effects and side effects coming from your suggestion. " +
            "You need to include either the solution to all, or comment what is left to do, currently left out of your answer.\n" +
            "Please include possible suggestions on refactoring of included files or answers if you think it is an improvement. " +
            "Possibly suggest moving functionality between files/classes or creating new files/classes."
    var gptAsk: String = gptPrePrompt
    var additionalFiles: List<String> = emptyList()
    private var sendCodeWithGptAskType: Int = SendCodeMethod.DONT_SEND.ordinal
    var doRefactorPrompt: String = "Please refactor enclosed code. Restructure it where it improves nonfunctional " +
            "attributes like readability, maintainability, extensibility, modularity and testability, " +
            "as well as reduces complexity. Without changing " +
            "its external behavior. If you are supplied with multiple files and classes, refactor in such a way that " +
            "where it is an improvement, you suggest adding, changing, moving and removing files, functions and classes. " +
            "Especially target reducing duplicate code, large classes, long methods, high coupling between classes and " +
            "lack of logical structure. Your answer should include the final look of all code, without left out " +
            "parts or with 'examples' instead of finalized solutions."
    var createTestQuestion: String = "Please create tests for enclosed code. " +
            "Please, make sure that coverage is at least 80% and tests have success and failure scenario. " +
            "Also, your answer should have only code.  " +
            "No intro our outro words and don't cut the code in the response. It should be fully written. " +
            " Thank you!"

    var checkBugsQuestion: String = "Please check enclosed code. " +
            "Please, find all possibilities for bugs, null pointers, memory leaks and other issues that can be improved for enclosed code. " +
            "Your answer should be only code and comments inside the code, above lines that it is targeting to improve.  " +
            "No intro or outro words and don't cut the code in the response. It should be fully written. " +
            " Thank you!"

    var writeDocsQuestion: String = "Please write documentation and comments for enclosed code. " +
            "Please, cover all methods and important or complicated logic, but do not change the code. " +
            "Do not cover obvious parts, like variables with proper names. " +
            "Also, your answer should have only code with documentation and comments. " +
            "No intro our outro words and don't cut the code in the response. It should be fully written. " +
            " Thank you!"

    fun shouldSendCode(): SendCodeMethod = try {
        SendCodeMethod.entries[sendCodeWithGptAskType]
    } catch (s: Exception) {
        SendCodeMethod.DONT_SEND
    }

    fun setShouldSendCode(method: SendCodeMethod) {
        sendCodeWithGptAskType = method.ordinal
    }

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    enum class SendCodeMethod {
        DONT_SEND,
        SEND_A_FILE,
        SEND_SELECTED_ONLY,
        SEND_FILE_AND_OTHERS,
    }

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }
}