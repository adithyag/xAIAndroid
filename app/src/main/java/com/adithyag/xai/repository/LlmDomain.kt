package com.adithyag.xai.repository

import com.adithyag.xai.network.XAiApi
import com.adithyag.xai.network.data.ChatCompletionsRequest

import com.adithyag.xai.network.data.Message
import com.adithyag.xai.network.data.Message.Image.ImageContent
import com.adithyag.xai.network.data.Message.Image.Text
import com.adithyag.xai.network.data.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

internal const val MODEL_DEFAULT = "grok-beta"
internal const val MODEL_VISION = "grok-vision-beta"

private const val SYSTEM_MESSAGE_AUTOCOMPLETE =
    "You are an auto complete service. Only provide one autocomplete response only"
private const val COUNT_SUGGESTIONS = 5

@Singleton
class LlmDomain @Inject constructor(val xAiApi: XAiApi) {

    internal suspend fun chat(systemMessage: String, messages: List<LlmMessage>): LlmMessage {
        val messageList = buildList<Message> {
            add(Message.Text(MessageRole.SYSTEM, systemMessage))
            if (messages.size > 1) {
                messages.subList(0, size - 1).forEach { message ->
                    add(message.toTextMessage())
                }
            }
            with(messages.last()) {
                if (images.isNotEmpty()) {
                    add(toImageMessage())
                } else {
                    add(toTextMessage())
                }
            }
        }
        val request = ChatCompletionsRequest(
            messages = messageList,
            model = if (messages.last().images.isEmpty()) MODEL_DEFAULT else MODEL_VISION
        )
        val response = xAiApi.getChatCompletions(request)
        return LlmMessage(
            response.choices.first().message.content.toString(),
            role = Role.Assistant
        )
    }

    internal suspend fun autocomplete(input: String): List<String> {
        val messageList = buildList<Message> {
            add(Message.Text(MessageRole.SYSTEM, SYSTEM_MESSAGE_AUTOCOMPLETE))
            add(Message.Text(MessageRole.USER, input))
        }
        val request = ChatCompletionsRequest(
            messages = messageList,
            model = MODEL_DEFAULT,
            n = COUNT_SUGGESTIONS,
        )
        val response = xAiApi.getChatCompletions(request)
        return response.choices.map { it.message.content.toString() }

    }

    private fun LlmMessage.toTextMessage() = Message.Text(
        role = when (role) {
            Role.Assistant -> MessageRole.ASSISTANT
            Role.User -> MessageRole.USER
        },
        content = msg
    )

    private fun LlmMessage.toImageMessage() = Message.Image(
        role = when (role) {
            Role.Assistant -> MessageRole.ASSISTANT
            Role.User -> MessageRole.USER
        },
        content = buildList {
            addAll(
                images.map {
                    ImageContent(
                        ImageContent.Url(
                            url = buildString {
                                append("data:")
                                append(it.mimeType)
                                append(";base64,")
                                append(it.base64)
                            }
                        )
                    )
                }
            )
            add(Text(msg))
        }
    )

    enum class Role {
        Assistant,
        User
    }

    data class LlmMessage(
        val msg: String,
        val images: List<Image> = emptyList<Image>(),
        val role: Role = Role.User,
    ) {
        data class Image(
            val mimeType: String,
            val base64: String,
        )
    }
}
