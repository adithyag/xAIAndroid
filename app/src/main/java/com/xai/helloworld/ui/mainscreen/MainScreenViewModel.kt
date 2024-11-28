package com.xai.helloworld.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.helloworld.network.XAiApi
import com.xai.helloworld.network.data.ChatCompletionsRequest
import com.xai.helloworld.network.data.MessageRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.xai.helloworld.network.data.Message as MessageData

@HiltViewModel
class MainScreenViewModel @Inject constructor(val xAiApi: XAiApi) : ViewModel() {
    private val systemMessage = MessageData(
        role = MessageRole.SYSTEM,
        content = "You are Grok, a helpful assistant."
    )

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    internal var messages = _messages.asStateFlow()

    fun onUserMessage(msg: String) {
        val newMessage = Message(msg, pending = true)
        _messages.value += newMessage
        viewModelScope.launch {
            val request = createChatCompletionRequest(_messages.value)
            val response = xAiApi.getChatCompletions(request)
            newMessage.pending = false
            onModelResponse(response.choices.first().message.content.toString())
        }
    }

    fun onModelResponse(response: String) {
        _messages.value += Message(response, Role.Assistant)
    }

    private fun createChatCompletionRequest(messages: List<Message>): ChatCompletionsRequest {
        val messages = listOf(systemMessage) + messages.map { it.toMessageData() }
        return ChatCompletionsRequest(messages = messages)
    }
}

enum class Role {
    Assistant,
    User
}

data class Message(
    val msg: String,
    val role: Role = Role.User,
    val id: Long = Companion.id++,
    var pending: Boolean = false
) {
    companion object {
        var id = 0L
    }
}

private fun Message.toMessageData() = MessageData(
    role = when (role) {
        Role.Assistant -> MessageRole.ASSISTANT
        Role.User -> MessageRole.USER
    },
    content = msg
)