package com.adithyag.xai.network

import com.adithyag.xai.network.data.ApiKeyResponse
import com.adithyag.xai.network.data.ChatCompletionsRequest
import com.adithyag.xai.network.data.ChatCompletionsResponse
import com.adithyag.xai.network.data.CompletionsRequest
import com.adithyag.xai.network.data.CompletionsResponse
import com.adithyag.xai.network.data.LanguageModel
import com.adithyag.xai.network.data.LanguageModelsResponse
import com.adithyag.xai.network.data.Model
import com.adithyag.xai.network.data.ModelsResponse
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
     * OpenAI compatible version of model listing with reduced information. This endpoint is
     * compatible with the OpenAI API.
     *
     * @return See [ModelsResponse]
     */
    @GET("models")
    suspend fun getModels(): ModelsResponse

    /**
     * List all language and embedding models available. This endpoint is compatible with the
     * OpenAI API.
     *
     * @param modelId The Id of model. It can be retrieved from [getModels]
     * @return
     */
    @GET("models/{modelId}")
    suspend fun getModel(@Path modelId: String): Model

    /**
     * Create a language model response for a given prompt. This endpoint is compatible with the
     * OpenAI API. See [CompletionsRequest]
     */
    @POST("completions")
    suspend fun getCompletions(@Body completionRequest: CompletionsRequest): CompletionsResponse

    /**
     * Create a language model response for a given chat conversation. This endpoint is compatible
     * with the OpenAI API.
     */
    @POST("chat/completions")
    suspend fun getChatCompletions(@Body chatCompletionRequest: ChatCompletionsRequest): ChatCompletionsResponse
}
