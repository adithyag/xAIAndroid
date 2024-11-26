package com.xai.helloworld.network

import com.xai.helloworld.network.data.ApiKeyResponse
import de.jensklingenberg.ktorfit.http.GET

interface XAiApi {
    @GET("api-key")
    suspend fun getApiKeyInfo(): ApiKeyResponse
}
