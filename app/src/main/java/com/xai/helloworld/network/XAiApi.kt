package com.xai.helloworld.network

import com.xai.helloworld.network.data.ApiKeyResponse
import com.xai.helloworld.network.data.CompletionsRequest
import com.xai.helloworld.network.data.CompletionsResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

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
