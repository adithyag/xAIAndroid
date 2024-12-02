package com.adithyag.xai.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Models response. See [com.adithyag.xai.network.XAiApi.getModels]
 *
 * @property data List of models available and their details. See [Model]
 * @property objectX The object type, which is always "list".
 */
@Serializable
data class ModelsResponse(
    val data: List<Model>,
    @SerialName("object")
    val objectX: String,
)

/**
 * Information about a single model. See [LanguageModelsResponse.models]
 *
 * @property id The model identifier, which can be referenced in the API endpoints.
 * @property created The Unix timestamp (in seconds) when the model was created.
 * @property objectX The object type, which is always "model".
 * @property ownedBy The organization that owns the model such as `xai`
 */
@Serializable
data class Model(
    val id: String,
    val created: Int,
    @SerialName("object")
    val objectX: String,
    @SerialName("owned_by")
    val ownedBy: String,
)



