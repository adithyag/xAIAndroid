package com.xai.helloworld.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Language models response. See [com.xai.helloworld.network.XAiApi.getLanguageModels]
 *
 * @property models List of language models available and their details. See [LanguageModel]
 * @constructor Create empty Language models response
 */
@Serializable
data class LanguageModelsResponse(
    val models: List<LanguageModel>
)

/**
 * Information about a single model. See [LanguageModelsResponse.models]
 *
 * @property id The model identifier, which can be referenced in the API endpoints.
 * @property fingerprint ?Response parameter to monitor changes in the backend
 * @property created The Unix timestamp (in seconds) when the model was created.
 * @property objectX The object type, which is always "model".
 * @property ownedBy The organization that owns the model such as `xai`
 * @property version Version name of the model
 * @property inputModalities List of input types that can be used to prompt this model
 * @property outputModalities List of output types that this model is capable of generating
 * @property promptTextTokenPrice Price of 10 billion text input tokens in USD
 * @property promptImageTokenPrice Price of 10 billion image input tokens in USD
 * @property completionTextTokenPrice Price of 10 billion text output tokens in USD
 */
@Serializable
data class LanguageModel(
    val id: String,
    val fingerprint: String,
    val created: Int,
    @SerialName("object")
    val objectX: String,
    @SerialName("owned_by")
    val ownedBy: String,
    val version: String,
    @SerialName("input_modalities")
    val inputModalities: List<String>,
    @SerialName("output_modalities")
    val outputModalities: List<String>,
    @SerialName("prompt_text_token_price")
    val promptTextTokenPrice: Int,
    @SerialName("prompt_image_token_price")
    val promptImageTokenPrice: Int,
    @SerialName("completion_text_token_price")
    val completionTextTokenPrice: Int
)



