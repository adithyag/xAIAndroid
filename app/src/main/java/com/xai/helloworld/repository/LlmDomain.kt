package com.xai.helloworld.repository

import com.xai.helloworld.network.XAiApi
import com.xai.helloworld.network.data.ChatCompletionsRequest

import com.xai.helloworld.network.data.Message
import com.xai.helloworld.network.data.Message.Image.ImageContent
import com.xai.helloworld.network.data.Message.Image.Text
import com.xai.helloworld.network.data.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

internal const val MODEL_DEFAULT = "grok-beta"
internal const val MODEL_VISION = "grok-vision-beta"

@Singleton
class LlmDomain @Inject constructor(val xAiApi: XAiApi) {

    private val systemMessage = Message.Text(
        role = MessageRole.SYSTEM,
        content = "You are Grok, a helpful assistant."
    )

    internal suspend fun chat(messages: List<LlmMessage>): LlmMessage {
        val messageList = buildList<Message> {
            add(systemMessage)
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
        val id: Long = Companion.id++,
        var pending: Boolean = false
    ) {
        companion object {
            private var id = 0L
        }

        data class Image(
            val mimeType: String,
            val base64: String,
        )
    }
}
