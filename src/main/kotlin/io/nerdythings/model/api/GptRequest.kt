package io.nerdythings.model.api

import com.google.gson.annotations.SerializedName

const val USER = "user"
data class GptRequest(
    @SerializedName("model")
    val model: String,
    @SerializedName("messages")
    val messages: List<GptRequestMessage>,
)

data class GptRequestMessage(
    @SerializedName("content")
    val content: String,
    @SerializedName("role")
    val role: String = USER,
)
