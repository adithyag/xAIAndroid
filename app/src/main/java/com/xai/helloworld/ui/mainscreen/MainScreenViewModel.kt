package com.xai.helloworld.ui.mainscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.helloworld.network.XAiApi
import com.xai.helloworld.network.data.CompletionsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(val xAiApi: XAiApi) : ViewModel() {
    internal var messages: List<Message> by mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            val apiKeyInfo = xAiApi.getApiKeyInfo()
            messages += Message(apiKeyInfo.toString())
        }
    }

    fun onUserMessage(msg: String) {
        val newMessage = Message(msg, pending = true)
        messages += newMessage
        viewModelScope.launch {
            val completionsResponse = xAiApi.getCompletions(CompletionsRequest(prompt = msg))
            newMessage.pending = false
            messages += Message(completionsResponse.choices.first().text, Role.Assistant)
        }
    }
}

enum class Role {
    System,
    Assistant,
    User
}

data class Message(
    val msg: String,
    val role: Role = Role.User,
    val id: Long = Companion.id++,
    var pending: Boolean = true
) {
    companion object {
        var id = 0L
    }
}