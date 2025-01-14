package com.adithyag.xai.ui.mainscreen

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adithyag.xai.repository.ImageRepository
import com.adithyag.xai.repository.LlmDomain
import com.adithyag.xai.repository.LlmDomain.LlmMessage
import com.adithyag.xai.repository.LlmDomain.Role
import com.adithyag.xai.repository.Persona
import com.adithyag.xai.repository.Personas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    val llmDomain: LlmDomain,
    val imageRepository: ImageRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    internal var messages = _messages.asStateFlow()

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    internal var images = _images.asStateFlow()

    private val _processing = MutableStateFlow(false)
    internal var processing = _processing.asStateFlow()

    private val _persona = MutableStateFlow<Persona>(Personas.DEFAULT)
    internal var persona = _persona.asStateFlow()

    fun onUserMessage(msg: String) {
        val newMessage = Message(
            type = Type.User,
            msg = msg,
            images = _images.value,
        )
        _messages.value += newMessage
        viewModelScope.launch {
            _processing.value = true
            try {
                _messages.value += llmDomain.chat(
                    _persona.value.systemMessage,
                    _messages.value.mapNotNull { it.toLlmMessage() }
                )
                    .toMessage()
            } catch (e: Exception) {
                _messages.value += Message(
                    type = Type.Error,
                    msg = e.message ?: e.toString(),
                )
            }
            _processing.value = false
        }
    }

    fun onImageAdded(uri: Uri?) {
        if (uri != null) {
            viewModelScope.launch {
                _images.value += Image(
                    uri = uri,
                    thumbnailPainter = BitmapPainter(
                        imageRepository.getThumbnailFromUri(uri).asImageBitmap()
                    ),
                    mimeType = imageRepository.getMimeType(uri) ?: "image/jpeg",
                    base64 = imageRepository.getBase64EncodedData(uri)
                )
            }
        }
    }

    fun onPersonaSelected(persona: Persona) {
        if (persona != _persona.value) {
            _persona.value = persona
            _messages.value = emptyList()
            _images.value = emptyList()
        }
    }

    fun onImageRemoved(image: Image) {
        _images.value -= image
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MainScreenViewModel", "onCleared")
    }
}

enum class Type {
    User,
    Assistant,
    Error
}

data class Message(
    val type: Type,
    val msg: String,
    val images: List<Image> = emptyList(),
)

private fun Message.toLlmMessage() = if (type == Type.Error) null else LlmMessage(
    msg = msg,
    images = images.map { LlmMessage.Image(it.mimeType, it.base64) },
    role = when (type) {
        Type.User -> Role.User
        Type.Assistant -> Role.Assistant
        Type.Error -> throw Exception("Error messages cannot be converted to LlmMessage")
    }
)

private fun LlmMessage.toMessage() = Message(
    type = if (role == Role.Assistant) Type.Assistant else Type.User,
    msg = msg
)

data class Image(
    val uri: Uri,
    val thumbnailPainter: BitmapPainter,
    val mimeType: String,
    val base64: String,
)
