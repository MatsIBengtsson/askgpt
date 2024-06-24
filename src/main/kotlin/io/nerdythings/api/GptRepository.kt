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
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GptRepository {
    private const val CHAT_URL = "https://api.openai.com/v1/chat/completions"
    private const val FILE_UPLOAD_URL = "https://api.openai.com/v1/files"
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
            val result = HttpClient.postRequest(CHAT_URL, headers, gptRequest, typeToken)
            runOnUiThread {
                callback.invoke(result.getMessageText(), null)
            }
            // We don't care about exposing error to a developer
        } catch (e: HttpClient.UnAuthorizedException) {
            runOnUiThread {
                callback.invoke(
                    null, "Unauthorized. " +
                            "Please go to menu Tools -> AskGPT -> AskGPT Settings and ensure you have set a valid ChatGPT API Key."
                )
            }
        } catch (e: Exception) {
            runOnUiThread {
                callback.invoke(null, e.message)
            }
        }
    }

    suspend fun askGptWithFiles(
        body: String,
        files: List<File>,
        callback: (result: String?, error: String?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("question", body)

        files.forEach { file ->
            requestBody.addFormDataPart("files", file.name, file.asRequestBody("application/octet-stream".toMediaTypeOrNull()))
        }

        val request = Request.Builder()
            .url(CHAT_URL) // Assuming the same URL is used
            .addHeader(HEADER_NAME, authHeaderValue)
            .post(requestBody.build())
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val responseBody = response.body?.string()
                runOnUiThread {
                    callback.invoke(responseBody, null)
                }
            }
        } catch (e: IOException) {
            runOnUiThread {
                callback.invoke(null, e.message)
            }
        }
    }

    suspend fun uploadSingleFile(file: File, callback: (fileId: String?, error: String?) -> Unit) = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val fileContent = file.readBytes()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("purpose", "assistants") // Adjust the purpose as needed
            .addFormDataPart("file", file.name, fileContent.toRequestBody("application/octet-stream".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url(FILE_UPLOAD_URL)
            .addHeader(HEADER_NAME, authHeaderValue)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful)
                    throw IOException("Unexpected code $response")
                val fileId = parseFileId(responseBody)
                runOnUiThread {
                    callback.invoke(fileId, null)
                }
            }
        } catch (e: IOException) {
            runOnUiThread {
                callback.invoke(null, e.message)
            }
        }
    }

    private fun parseFileId(responseBody: String?): String? {
        return responseBody?.let {
            val json = JSONObject(it)
            json.optString("id")
        }
    }

    suspend fun uploadFiles(files: List<File>): List<String> {
        val fileIds = mutableListOf<String>()
        for (file in files) {
            uploadSingleFile(file) { fileId, error ->
                if (error == null && fileId != null) {
                    fileIds.add(fileId)
                } else {
                    println("Error uploading file and getting fileID for it: $file, $error")
                }
            }
        }
        return fileIds
    }
}