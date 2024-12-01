package com.xai.helloworld.ui.mainscreen

import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xai.helloworld.repository.ImageRepository
import com.xai.helloworld.repository.LlmDomain
import com.xai.helloworld.repository.LlmDomain.LlmMessage
import com.xai.helloworld.repository.LlmDomain.Role
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
                _messages.value += llmDomain.chat(_messages.value.mapNotNull { it.toLlmMessage() })
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

    fun onImageRemoved(image: Image) {
        _images.value -= image
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
