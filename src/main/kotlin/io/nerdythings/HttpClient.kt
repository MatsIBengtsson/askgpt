package io.nerdythings

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpClient {
    private const val JSON_TYPE = "application/json; charset=utf-8"
    private const val POST = "POST"

    private val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    class ApiException(override val message: String) : IOException()
    class UnAuthorizedException(override val message: String) : IOException()

    suspend fun <T, A> postRequest(
        url: String,
        headers: Map<String, String>,
        body: A,
        typeOf: TypeToken<T>,
    ): T & Any = withContext(Dispatchers.IO) {
        val jsonType: MediaType? = JSON_TYPE.toMediaTypeOrNull()
        val bodyString = gson.toJson(body)
        val requestBody = bodyString.toRequestBody(jsonType)
        val request = Request.Builder()
            .method(POST, requestBody)
            .headers(headers.toHeaders())
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        if (response.code == 401) {
            throw UnAuthorizedException("Unauthorized \n ${response.code} ${response.body?.string()}")
        } else if (response.isSuccessful) {
            return@withContext gson.fromJson(response.body?.string(), typeOf)
                ?: throw ApiException("Empty response ${response.code}")
        } else {
            throw ApiException("Error ${response.code} ${response.body?.string()}")
        }
    }
}