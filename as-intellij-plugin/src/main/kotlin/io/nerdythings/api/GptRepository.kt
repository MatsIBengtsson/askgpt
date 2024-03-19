package io.nerdythings.api

import com.google.gson.reflect.TypeToken
import io.nerdythings.HttpClient
import io.nerdythings.utils.runOnUiThread
import io.nerdythings.model.api.GptRequest
import io.nerdythings.model.api.GptRequestMessage
import io.nerdythings.model.api.GptResponse
import io.nerdythings.preferences.AppSettingsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object GptRepository {
    private const val URL = "https://api.openai.com/v1/chat/completions"
    private val authHeaderValue: String
        get() = "Bearer ${AppSettingsState.instance.gptToken}"
    private const val HEADER_NAME = "Authorization"

    private val typeToken: TypeToken<GptResponse> by lazy {
        TypeToken.get(GptResponse::class.java)
    }

    suspend fun askGpt(
        body: String,
        callback: (result: String?, error: String?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val headers = mapOf(
            HEADER_NAME to authHeaderValue
        )
        val gptRequest = GptRequest(
            model = AppSettingsState.instance.gptModel,
            messages = listOf(
                GptRequestMessage(
                    content = body,
                )
            )
        )
        try {
            val result = HttpClient.postRequest(URL, headers, gptRequest, typeToken)
            runOnUiThread {
                callback.invoke(result.getMessageText(), null)
            }
            // We don't care about exposing error to a developer
        } catch (e: HttpClient.UnAuthorizedException) {
            runOnUiThread {
                callback.invoke(
                    null, "Unauthorized. " +
                            "Please go to menu Tools -> AskGPT Preferences and check if you set ChatGPT API Key."
                )
            }
        } catch (e: Exception) {
            runOnUiThread {
                callback.invoke(null, e.message)
            }
        }
    }
}