package com.adithyag.xai.di

import android.util.Log
import com.adithyag.xai.BuildConfig
import com.adithyag.xai.network.XAiApi
import com.adithyag.xai.network.createXAiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
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

@Module
@InstallIn(SingletonComponent::class)
object XAiModule {
    @Provides
    fun provideXAiApi(): XAiApi {
        return Ktorfit.Builder()
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
    }
}