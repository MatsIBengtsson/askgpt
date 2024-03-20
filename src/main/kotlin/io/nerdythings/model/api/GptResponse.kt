package io.nerdythings.model.api

import com.google.gson.annotations.SerializedName

data class GptResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("object")
    val objectR: String?,
    @SerializedName("created")
    val created: Long?,
    @SerializedName("model")
    val model: String?,
    @SerializedName("choices")
    val choices: List<GptChoiceResponse>?,
    @SerializedName("usage")
    val usage: GptUsageResponse?,
    @SerializedName("system_fingerprint")
    val fingerprint: String?,
) {
    fun getMessageText(): String {
        val responseBuilder = StringBuilder()
        choices?.let {
            choices.forEach {
                val message = it.message
                message?.content?.apply {
                    responseBuilder.append(this)
                }
            }
        }
        return responseBuilder.toString()
    }
}

data class GptChoiceResponse(
    @SerializedName("index")
    val index: Int?,
    @SerializedName("message")
    val message: GptMessageResponse?,
    @SerializedName("logprobs")
    val logprobs: String?,
    @SerializedName("finish_reason")
    val finishReason: String?,

)

data class GptMessageResponse(
    @SerializedName("role")
    val role: String?,
    @SerializedName("content")
    val content: String?,
)

data class GptUsageResponse(
    @SerializedName("prompt_tokens")
    val promptTokens: Long?,
    @SerializedName("completion_tokens")
    val completionTokens: Long?,
    @SerializedName("total_tokens")
    val totalTokens: Long?,
)
