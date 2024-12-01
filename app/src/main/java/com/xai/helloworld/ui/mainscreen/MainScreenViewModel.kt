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

    fun onUserMessage(msg: String) {
        val newMessage = Message(
            role = Role.User,
            msg = msg,
            images = _images.value,
            pending = true
        )
        _messages.value += newMessage
        viewModelScope.launch {
            val assistantMessage = llmDomain.chat(
                _messages.value.map { it.toLlmMessage() }
            )
            newMessage.pending = false
            _messages.value += assistantMessage.toMessage()
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

data class Message(
    val role: Role,
    val msg: String,
    val images: List<Image> = emptyList(),
    var pending: Boolean = false
)

private fun Message.toLlmMessage() = LlmMessage(
    msg = msg,
    images = images.map { LlmMessage.Image(it.mimeType, it.base64) },
    role = role
)

private fun LlmMessage.toMessage() = Message(role = role, msg = msg)

data class Image(
    val uri: Uri,
    val thumbnailPainter: BitmapPainter,
    val mimeType: String,
    val base64: String,
)
