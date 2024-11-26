package com.xai.helloworld.network

import android.util.Log
import com.xai.helloworld.BuildConfig
import com.xai.helloworld.network.data.ApiKeyResponse
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.GET
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

fun getXAiApi(): XAiApi = Ktorfit.Builder()
    .baseUrl("https://api.x.ai/v1/")
    .httpClient(Android) {
        install(UserAgent) {
            agent = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}/" +
                    "${BuildConfig.VERSION_CODE}"
        }
        install(ContentNegotiation) {
            json()
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
        expectSuccess = true
    }
    .build()
    .createXAiApi()

interface XAiApi {
    @GET("api-key")
    suspend fun getApiKeyInfo(): ApiKeyResponse
}
