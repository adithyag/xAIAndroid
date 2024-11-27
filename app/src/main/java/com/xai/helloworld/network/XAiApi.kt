package com.xai.helloworld.network

import com.xai.helloworld.network.data.ApiKeyResponse
import com.xai.helloworld.network.data.CompletionsRequest
import com.xai.helloworld.network.data.CompletionsResponse
import com.xai.helloworld.network.data.LanguageModel
import com.xai.helloworld.network.data.LanguageModelsResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface XAiApi {
    /**
     * Get information about an API key, including name, status, permissions and users who created
     * or modified this key. See [ApiKeyResponse]
     */
    @GET("api-key")
    suspend fun getApiKeyInfo(): ApiKeyResponse

    /**
     * List all language models available. See [LanguageModelsResponse]
     */
    @GET("language-models")
    suspend fun getLanguageModels(): LanguageModelsResponse

    /**
     * Get information about a language model using its ID.
     *
     * @param modelId The Id of model. It can be retrieved from [getLanguageModels]
     * @return
     */
    @GET("language-models/{modelId}")
    suspend fun getLanguageModel(@Path modelId: String): LanguageModel

    /**
     * Create a language model response for a given prompt. This endpoint is compatible with the
     * OpenAI API. See [CompletionsRequest]
     */
    @POST("completions")
    suspend fun getCompletions(@Body completionRequest: CompletionsRequest): CompletionsResponse
}
