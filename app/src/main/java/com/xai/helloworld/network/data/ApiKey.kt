package com.xai.helloworld.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Api key response
 *
 * @property acls access control list for the api key; ex -"api-key:endpoint:*", "api-key:model:*",
 * @property apiKeyBlocked blocked status of the api key
 * @property apiKeyDisabled disabled status of the api key
 * @property apiKeyId id of the api key
 * @property createTime ISO 8061 formatted api key creation time; ex- 2024-11-22T19:41:30.583654Z
 * @property modifiedBy id of the user who modified the api key
 * @property modifyTime ISO 8061 formatted api key modification time; ex- 2024-11-22T19:41:30.583654Z
 * @property name name of the api key
 * @property redactedApiKey redacted version of the api key
 * @property teamBlocked blocked status of the team
 * @property teamId id of the team
 * @property userId id of the user
 * @constructor Create empty Api key response
 */
@Serializable
data class ApiKeyResponse(
    val acls: List<String>,
    @SerialName("api_key_blocked")
    val apiKeyBlocked: Boolean,
    @SerialName("api_key_disabled")
    val apiKeyDisabled: Boolean,
    @SerialName("api_key_id")
    val apiKeyId: String,
    @SerialName("create_time")
    val createTime: String,
    @SerialName("modified_by")
    val modifiedBy: String,
    @SerialName("modify_time")
    val modifyTime: String,
    val name: String,
    @SerialName("redacted_api_key")
    val redactedApiKey: String,
    @SerialName("team_blocked")
    val teamBlocked: Boolean,
    @SerialName("team_id")
    val teamId: String,
    @SerialName("user_id")
    val userId: String
)
