package com.xai.helloworld.network

import android.util.Log
import com.xai.helloworld.BuildConfig
import com.xai.helloworld.network.data.ApiKeyResponse
import com.xai.helloworld.network.data.CompletionsRequest
import com.xai.helloworld.network.data.CompletionsResponse
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface XAiApi {
    /**
     * Get information about an API key, including name, status, permissions and users who created
     * or modified this key. See [ApiKeyResponse]
     */
    @GET("api-key")
    suspend fun getApiKeyInfo(): ApiKeyResponse

    /**
     * Create a language model response for a given prompt. This endpoint is compatible with the
     * OpenAI API. See [CompletionsRequest]
     */
    @POST("completions")
    suspend fun getCompletions(@Body completionRequest: CompletionsRequest): CompletionsResponse
}

fun getXAiApi(): XAiApi = Ktorfit.Builder()
    .baseUrl("https://api.x.ai/v1/")
    .httpClient(Android) {
        install(UserAgent) {
            agent = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}/" +
                    "${BuildConfig.VERSION_CODE}"
        }
        install(ContentNegotiation) {
            json(Json {
                // server handles defaults for missing fields
                explicitNulls = false
                // to allow setting defaults for required fields
                encodeDefaults = true
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(BuildConfig.API_KEY, null)
                }
            }
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", message)
                }
            }
            sanitizeHeader { header ->
                header == "Authorization"
            }
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
        expectSuccess = true
    }
    .build()
    .createXAiApi()