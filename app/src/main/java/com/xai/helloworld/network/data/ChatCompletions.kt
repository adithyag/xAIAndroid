package com.xai.helloworld.network.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * Completions request for a given prompt. See [com.xai.helloworld.network.XAiApi.getCompletions]
 *
 * @property messages A list of messages that make up the the chat conversation. Different models
 * support different message types, such as image and text. See [Message]
 * @property model Specifies the model to be used for the request.
 * @property frequencyPenalty Number between -2.0 and 2.0. Positive values penalize new tokens based
 * on their existing frequency in the text so far, decreasing the model's likelihood to repeat the
 * same line verbatim.
 * @property logitBias A JSON object that maps tokens (specified by their token ID in the tokenizer)
 * to an associated bias value from -100 to 100. Mathematically, the bias is added to the logits
 * generated by the model prior to sampling. The exact effect will vary per model, but values
 * between -1 and 1 should decrease or increase likelihood of selection; values like -100 or 100
 * should result in a ban or exclusive selection of the relevant token.
 * @property logprobs Whether to return log probabilities of the output tokens or not. If true,
 * returns the log probabilities of each output token returned in the content of message.
 * @property maxTokens The maximum number of tokens that can be generated in the chat completion.
 * This value can be used to control costs for text generated via API.
 * @property n How many chat completion choices to generate for each input message. Note that you
 * will be charged based on the number of generated tokens across all of the choices. Keep n as 1 to
 * minimize costs.
 * @property presencePenalty Number between -2.0 and 2.0. Positive values penalize new tokens based
 * on whether they appear in the text so far, increasing the model's likelihood to talk about new
 * topics.
 * @property responseFormat ***Unknown***
 * @property seed If specified, our system will make a best effort to sample deterministically, such
 * that repeated requests with the same seed and parameters should return the same result.
 * Determinism is not guaranteed, and you should refer to the system_fingerprint response parameter
 * to monitor changes in the backend.
 * @property stop Up to 4 sequences where the API will stop generating further tokens. The returned
 * text will not contain the stop sequence.
 * @property stream If set, partial message deltas will be sent. Tokens will be sent as data-only
 * server-sent events as they become available, with the stream terminated by a `data: [DONE]`
 * message.
 * @property streamOptions Options for streaming response. Only set this when you set stream: true.
 * See [StreamOptions].
 * @property temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8
 * will make the output more random, while lower values like 0.2 will make it more focused and
 * deterministic. We generally recommend altering this or `top_p` but not both.
 * @property toolsChoice ***Unknown***
 * @property tools A list of tools the model may call. Currently, only functions are supported as a
 * tool. Use this to provide a list of functions the model may generate JSON inputs for. A max of
 * 128 functions are supported.
 * @property topLogprobs An integer between 0 and 20 specifying the number of most likely tokens to
 * return at each token position, each with an associated log probability. logprobs must be set to
 * true if this parameter is used.
 * @property topP An alternative to sampling with temperature, called nucleus sampling, where the
 * model considers the results of the tokens with `top_p` probability mass. So 0.1 means only the
 * tokens comprising the top 10% probability mass are considered. We generally recommend altering
 * this or temperature but not both.
 * @property user A unique identifier representing your end-user, which can help xAI to monitor and
 * detect abuse.
 */

@Serializable
data class ChatCompletionsRequest(
    val messages: List<Message>,
    val model: String,
    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = null,
    @SerialName("logit_bias")
    val logitBias: Map<String, Int>? = null,
    val logprobs: Boolean? = null,
    @SerialName("max_tokens")
    val maxTokens: Int? = null,
    val n: Int? = null,
    @SerialName("presence_penalty")
    val presencePenalty: Double? = null,
    @SerialName("response_format")
    val responseFormat: String? = null,
    val seed: Int? = null,
    val stop: List<String>? = null,
    val stream: Boolean? = null,
    @SerialName("stream_options")
    val streamOptions: StreamOptions? = null,
    val temperature: Double? = null,
    @SerialName("tools_choice")
    val toolsChoice: String? = null,
    // TODO must add for tool serialization such as functions
    val tools: List<String>? = null,
    @SerialName("top_logprobs")
    val topLogprobs: Int? = null,
    @SerialName("top_p")
    val topP: Double? = null,
    val user: String? = null,
) {
    init {
        if (frequencyPenalty != null) {
            require(frequencyPenalty in -2.0..2.0) {
                "frequencyPenalty must be between -2.0 and 2.0 but is $frequencyPenalty"
            }
        }
        logitBias?.forEach { (token, bias) ->
            require(bias in -100..100) {
                "logit bias must be between -100 and 100 but is $bias for token $token"
            }
        }
        if (presencePenalty != null) {
            require(presencePenalty in -2.0..2.0) {
                "presencePenalty must be between -2.0 and 2.0 but is $presencePenalty"
            }
        }
        if (stop != null) {
            require(stop.size <= 4) {
                "stop may have a maximum of 4 sequences but has ${stop.size} sequences"
            }
        }
        if (streamOptions != null) {
            require(stream == true) { "If streamOptions is set, stream must be true" }
        }
        if (temperature != null) {
            require(temperature in 0.0..2.0) {
                "temperature must be between 0.0 and 2.0 but is $temperature"
            }
        }
        if (topLogprobs != null) {
            require(logprobs == true) { "topprobs must be true if topLogprobs is set" }
            require(topLogprobs in 0..20) {
                "topLogprobs must be between 0 and 20 but is $topLogprobs"
            }
        }
    }
}

object MessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Message")

    override fun serialize(encoder: Encoder, value: Message) {
        when (value) {
            is Message.Text -> encoder.encodeSerializableValue(Message.Text.serializer(), value)
            is Message.Image -> encoder.encodeSerializableValue(Message.Image.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): Message {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("MessageSerializer only supports JSON currently")
        val jsonObj = jsonDecoder.decodeJsonElement().jsonObject

        // Inspect the `content` field to decide the type of message
        return when (val contentElement = jsonObj["content"]) {
            is JsonPrimitive -> jsonDecoder.json.decodeFromJsonElement(
                Message.Text.serializer(),
                jsonObj
            )

            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(
                Message.Image.serializer(),
                jsonObj
            )

            else -> throw SerializationException("Unknown content type in Message: $contentElement")
        }
    }

}

@Serializable(with = MessageSerializer::class)
sealed class Message {
    /**
     * Holds information about a single message in a chat completion. See
     * [ChatCompletionsRequest.messages]
     *
     * @property role The role of the messages author. See [MessageRole]
     * @property content The contents of the message
     */
    @Serializable
    data class Text(
        val role: MessageRole,
        val content: String
    ) : Message()

    /**
     * An array of content parts with a defined type. Supported options differ based on the model
     * being used to generate the response. Can contain text or image inputs. See
     * [ChatCompletionsRequest.messages] and [Content]
     *
     * @property role The role of the messages author. See [MessageRole]
     * @property content The contents of the message
     */
    @Serializable
    data class Image(
        val role: MessageRole,
        val content: List<Content>
    ) : Message() {

        @Serializable
        sealed class Content

        /**
         * Text input to the model
         */
        @Serializable
        @SerialName("text")
        data class Text(
            val text: String,
        ) : Content()

        /**
         * Image input to the model
         *
         * @property imageUrl Either a URL of the image or the base64 encoded image data.
         */
        @Serializable
        @SerialName("image_url")
        data class ImageContent(
            @SerialName("image_url")
            val imageUrl: Url
        ) : Content() {
            /**
             * Encodes an image
             *
             * @property url Either a URL of the image or the base64 encoded image data.
             * @property detail Specifies the detail level of the image. See [Detail]
             */
            @Serializable
            data class Url(
                val url: String,
                val detail: Detail? = null,
            ) {
                enum class Detail {
                    @SerialName("auto")
                    AUTO,

                    @SerialName("low")
                    LOW,

                    @SerialName("high")
                    HIGH,
                }

                override fun toString() = buildString {
                    append("Url(url=")
                    if (url.length > 14) {
                        append(url.take(7))
                        append("...")
                        append(url.takeLast(7))
                    } else {
                        append(url)
                    }
                    if (detail != null) {
                        append(", detail=$detail")
                    }
                }
            }
        }
    }
}


/**
 * The role of the author of the message. See [Message.role]
 */
enum class MessageRole {
    /**
     * Corresponds to the input message usually from developer to instruct the model.
     */
    @SerialName("system")
    SYSTEM,

    /**
     * Corresponds to the input message from the user to the model
     */
    @SerialName("user")
    USER,

    /**
     * Corresponds to the output message from the model to the user that is fed back as input to
     * provide the history of conversations for context.
     */
    @SerialName("assistant")
    ASSISTANT,
}

/**
 * Stream options. See [CompletionsRequest.streamOptions] and [ChatCompletionsRequest.streamOptions]
 *
 * @property includeUsage If set, an additional chunk will be streamed before the data: [DONE]
 * message. The usage field on this chunk shows the token usage statistics for the entire request,
 * and the choices field will always be an empty array. All other chunks will also include a usage
 * field, but with a null value.
 */
@Serializable
data class StreamOptions(
    @SerialName("include_usage")
    val includeUsage: Boolean? = null,
)

/**
 * Completions response. See [com.xai.helloworld.network.XAiApi.getCompletions]
 *
 * @property id A unique identifier for the completion.
 * @property objectX The object type, which is always "chat.completion"
 * @property created The Unix timestamp (in seconds) of when the completion was created.
 * @property model The model used for completion.
 * @property choices The list of chat completion choices the model generated for the input prompt.
 * See [ChatCompletionChoice]
 * @property usage Usage statistics for the completion request. See [Usage]
 * @property systemFingerprint This fingerprint represents the backend configuration that the model
 * runs with. Can be used in conjunction with the seed request parameter to understand when backend
 * changes have been made that might impact determinism.
 */
@Serializable
data class ChatCompletionsResponse(
    val id: String,
    @SerialName("object")
    val objectX: String,
    val created: Long,
    val model: String,
    val choices: List<ChatCompletionChoice>,
    val usage: Usage,
    @SerialName("system_fingerprint")
    val systemFingerprint: String,
)

/**
 * A choice the model generated for the input prompt. See [ChatCompletionsResponse.choices]
 * @property index index of this potential response in the list
 * @property message
 * @property finishReason The reason the model stopped generating tokens. This will be `stop` if the
 * model hit a natural stop point or a provided stop sequence, `length` if the maximum number of
 * tokens specified in the request was reached, or `content_filter` if content was omitted due to a
 * flag from our content filters.
 */
@Serializable
data class ChatCompletionChoice(
    val index: Int,
    val message: ChatCompletionMessage,
    @SerialName("finish_reason")
    val finishReason: String,
)

/**
 * Chat completion message
 *
 * @property role The role of the author of this message.
 * @property content The contents of the message.
 * @property refusal The refusal message generated by the model.
 */
@Serializable
data class ChatCompletionMessage(
    val role: MessageRole,
    val content: String?,
    val refusal: String?,
)

/**
 * Usage statistics for the completion request. See [CompletionsResponse.usage]
 *
 * @property promptTokens Number of tokens in the prompt.
 * @property completionTokens Number of tokens in the generated completion.
 * @property totalTokens Total number of tokens used in the request (prompt + completion).
 * @property promptTokensDetails Breakdown of tokens used in the prompt. See [PromptTokensDetails]
 */
@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("prompt_tokens_details")
    val promptTokensDetails: PromptTokensDetails,
)

/**
 * Breakdown of tokens used in the prompt.
 *
 * @property textTokens Text input tokens present in the prompt.
 * @property audioTokens Audio input tokens present in the prompt.
 * @property imageTokens Image input tokens present in the prompt.
 * @property cachedTokens Cached tokens present in the prompt.
 */
@Serializable
data class PromptTokensDetails(
    @SerialName("text_tokens")
    val textTokens: Int,
    @SerialName("audio_tokens")
    val audioTokens: Int,
    @SerialName("image_tokens")
    val imageTokens: Int,
    @SerialName("cached_tokens")
    val cachedTokens: Int
)
