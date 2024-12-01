package com.xai.helloworld.ui.mainscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xai.helloworld.R
import com.xai.helloworld.repository.LlmDomain.Role
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme
import kotlinx.coroutines.flow.StateFlow

private val jpegRequest = PickVisualMediaRequest(PickVisualMedia.SingleMimeType("image/jpeg"))

@Composable
internal fun MainScreen(viewModel: MainScreenViewModel) {
    MainScreen(
        viewModel.messages,
        viewModel::onUserMessage,
        viewModel.images,
        viewModel::onImageAdded,
        viewModel::onImageRemoved
    )
}

private val THUMBNAIL_SIZE = 100.dp

@Composable
fun MainScreen(
    messages: StateFlow<List<Message>>,
    onUserMessage: (String) -> Unit,
    images: StateFlow<List<Image>>,
    onImageSelected: (Uri?) -> Unit,
    onImageDeleted: (Image) -> Unit
) {
    XAIHelloWorldTheme {
        val launcher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            onImageSelected(uri)
        }
        Column(
            modifier = Modifier
                .background(colorScheme.background)
                .padding(16.dp)
                .safeDrawingPadding(),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val messages by messages.collectAsState()
                val images by images.collectAsState()
                val hasImages by remember { derivedStateOf { images.isNotEmpty() } }
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .paint(
                            painterResource(R.drawable.ic_launcher_foreground),
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.Center,
                            alpha = 0.05f,
                        ),
                    reverseLayout = true,
                ) {
                    if (hasImages) {
                        item {
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .height(THUMBNAIL_SIZE)
                            )
                        }
                    }
                    items(messages.asReversed()) {
                        ChatMessage(it)
                    }
                }
                if (hasImages) {
                    Row(Modifier.align(Alignment.BottomEnd)) {
                        for (image in images) {
                            Thumbnail(onImageDeleted, image)
                        }
                    }
                }
            }
            ChatInput(onUserMessage) {
                launcher.launch(jpegRequest)
            }
        }
    }
}

@Composable
private fun Thumbnail(
    onImageDeleted: (Image) -> Unit,
    image: Image
) {
    BadgedBox(
        {
            Badge {
                Icon(
                    Icons.Filled.Close,
                    "close",
                    Modifier.clickable(onClick = { onImageDeleted(image) })
                )
            }
        }, Modifier.padding(end = 20.dp)
    ) {
        Image(
            image.thumbnailPainter,
            "attached image",
            Modifier.size(THUMBNAIL_SIZE),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ChatMessage(message: Message) {
    Row {
        val icon = if (message.role == Role.Assistant) R.drawable.grok else R.drawable.user
        val desc = if (message.role == Role.Assistant) "grok icon" else "user icon"
        Image(
            ImageVector.vectorResource(icon),
            contentDescription = desc,
            Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp)
                .background(
                    when (message.role) {
                        Role.Assistant -> colorScheme.primary
                        Role.User -> {
                            if (message.pending) colorScheme.tertiary
                            else Color.Transparent
                        }
                    },
                    shape = shapes.small
                )
                .padding(4.dp)
                .fillMaxWidth(),
        ) {
            SelectionContainer {
                Text(
                    text = message.msg,
                    style = typography.bodyLarge,
                    color = when (message.role) {
                        Role.Assistant -> colorScheme.onSecondaryContainer
                        Role.User -> {
                            if (message.pending) colorScheme.onTertiary
                            else colorScheme.onPrimaryContainer
                        }

                    }
                )
            }
            if (message.images.isNotEmpty()) {
                Row {
                    for (image in message.images) {
                        Image(
                            image.thumbnailPainter,
                            "attached image",
                            Modifier.size(THUMBNAIL_SIZE),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInput(onUserMessage: (String) -> Unit, onImageClick: () -> Unit) {
    var currentUserInput by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            placeholder = @Composable {
                Text(
                    text = "Ask xAI anything",
                    color = colorScheme.inverseOnSurface,
                    style = typography.titleLarge,
                )
            },
            value = currentUserInput,
            trailingIcon = @Composable {
                SendIcons(
                    currentUserInput.isNotBlank(),
                    onSend = {
                        onUserMessage(currentUserInput)
                        currentUserInput = ""
                    },
                    onImageClick = onImageClick
                )
            },
            onValueChange = { currentUserInput = it },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = colorScheme.primary,
                unfocusedContainerColor = colorScheme.primary,
                cursorColor = colorScheme.onPrimary,
            ),
            shape = shapes.small,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text,
            )
        )
    }
}

@Composable
fun SendIcons(enabled: Boolean, onSend: () -> Unit, onImageClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onImageClick, modifier = Modifier.padding(end = 8.dp)) {
            Icon(
                ImageVector.vectorResource(R.drawable.attach_image),
                "Attach image",
                Modifier.size(36.dp),
                tint = colorScheme.onPrimary,
            )
        }
        IconButton(
            onClick = onSend,
            modifier = Modifier.padding(end = 8.dp),
            enabled = enabled,
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.send),
                "Send message to Grok",
                Modifier.size(36.dp),
                tint = if (enabled) colorScheme.onPrimary else colorScheme.inverseSurface
            )
        }
    }
}

@PreviewS22Ultra
@Composable
fun Preview() {
    MainScreenPreview()
}
