package com.xai.helloworld.ui.mainscreen

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.xai.helloworld.R
import com.xai.helloworld.repository.Persona
import com.xai.helloworld.ui.PreviewS22Ultra
import com.xai.helloworld.ui.theme.Dimensions
import com.xai.helloworld.ui.theme.Dimensions.AppMargin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

private val jpegRequest = PickVisualMediaRequest(PickVisualMedia.SingleMimeType("image/jpeg"))

@Composable
internal fun MainScreen(viewModel: MainScreenViewModel, onInfoClicked: () -> Unit) {
    MainScreen(
        viewModel.messages,
        viewModel::onUserMessage,
        viewModel.images,
        viewModel.processing,
        viewModel.persona,
        viewModel::onPersonaSelected,
        viewModel::onImageAdded,
        viewModel::onImageRemoved,
        onInfoClicked,
    )
}

@Composable
fun MainScreen(
    messages: StateFlow<List<Message>>,
    onUserMessage: (String) -> Unit,
    images: StateFlow<List<Image>>,
    processing: StateFlow<Boolean>,
    persona: StateFlow<Persona>,
    onPersonaSelected: (Persona) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onImageDeleted: (Image) -> Unit,
    onInfoClick: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        onImageSelected(uri)
    }
    Column(
        modifier = Modifier
            .background(colorScheme.background)
            .padding(AppMargin)
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
            val persona by persona.collectAsState()
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
                if (processing.value) {
                    item { ProcessingIndicator(persona) }
                }
                if (hasImages) {
                    item {
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(Dimensions.ThumbnailSize)
                        )
                    }
                }
                items(messages.asReversed()) {
                    ChatMessage(it, persona)
                }
            }
            if (hasImages) {
                Row(Modifier.align(Alignment.BottomEnd)) {
                    for (image in images) {
                        Thumbnail(onImageDeleted, image)
                    }
                }
            }

            Row(Modifier.align(Alignment.TopEnd)) {
                SettingsButton(persona, onPersonaSelected)
                InfoButton(onInfoClick)
            }
        }
        ChatInput(onUserMessage) {
            launcher.launch(jpegRequest)
        }
    }
}

@Composable
fun SettingsButton(currentPersona: Persona, onPersonaSelected: (Persona) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    IconButton(
        onClick = { showDialog = true },
    ) {
        Icon(
            Icons.Filled.Settings,
            "Settings",
            Modifier.size(Dimensions.IconButton),
            colorScheme.onPrimary,
        )
    }
    if (showDialog) {
        PersonaChoiceDialog(
            selectedPersona = currentPersona,
            onDismiss = { showDialog = false },
            onPersonaSelected = {
                Log.d("Settings", "Selected persona: ${it}")
                onPersonaSelected(it)
            }
        )
    }
}

@Composable
fun InfoButton(onInfoClick: () -> Unit) {
    IconButton(
        onClick = onInfoClick,
    ) {
        Icon(
            Icons.Filled.Info,
            "info",
            Modifier.size(Dimensions.IconButton),
            colorScheme.onPrimary,
        )
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
        }, Modifier.padding(end = Dimensions.BadgePadding)
    ) {
        Image(
            image.thumbnailPainter,
            "attached image",
            Modifier.size(Dimensions.ThumbnailSize),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ChatMessage(message: Message, persona: Persona) {
    Row {
        val icon = when (message.type) {
            Type.Assistant -> persona.vectorDrawableId
            Type.User -> R.drawable.user
            Type.Error -> R.drawable.ic_computer
        }
        val desc = if (message.type == Type.Assistant) "${persona.name} icon" else "user icon"
        Image(
            ImageVector.vectorResource(icon),
            contentDescription = desc,
            Modifier.size(Dimensions.SmallIcon)
        )
        Column(
            modifier = Modifier
                .padding(bottom = Dimensions.RowItemPadding, start = Dimensions.RowItemPadding)
                .background(
                    when (message.type) {
                        Type.User -> Color.Transparent
                        Type.Assistant -> colorScheme.primary
                        Type.Error -> colorScheme.errorContainer
                    },
                    shape = shapes.small
                )
                .padding(Dimensions.ContainerInset)
                .fillMaxWidth(),
        ) {
            SelectionContainer {
                Text(
                    text = message.msg,
                    style = typography.bodyLarge,
                    color = when (message.type) {
                        Type.User -> colorScheme.onSecondaryContainer
                        Type.Assistant -> colorScheme.onPrimaryContainer
                        Type.Error -> colorScheme.onErrorContainer
                    }
                )
            }
            if (message.images.isNotEmpty()) {
                Row {
                    for (image in message.images) {
                        Image(
                            image.thumbnailPainter,
                            "attached image",
                            Modifier.size(Dimensions.ThumbnailSize),
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
        IconButton(
            onClick = onImageClick,
            modifier = Modifier.padding(end = Dimensions.RowItemPadding)
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.attach_image),
                "Attach image",
                Modifier.size(Dimensions.IconButton),
                tint = colorScheme.onPrimary,
            )
        }
        IconButton(
            onClick = onSend,
            modifier = Modifier.padding(end = Dimensions.RowItemPadding),
            enabled = enabled,
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.send),
                "Send message to Grok",
                Modifier.size(Dimensions.IconButton),
                tint = if (enabled) colorScheme.onPrimary else colorScheme.inverseSurface
            )
        }
    }
}

@Composable
fun ProcessingIndicator(persona: Persona) {
    // Create a state to hold the rotation value
    var rotation by remember { mutableStateOf(0f) }

    // Launch a coroutine to continuously update the rotation
    LaunchedEffect(Unit) {
        while (true) {
            // Update the rotation value by 1 degree per frame
            rotation += 1f
            if (rotation >= 360f) {
                rotation = 0f // Reset the rotation to 0 when it reaches 360
            }
            delay(16) // Delay for ~60fps (1 frame per 16ms)
        }
    }

    // Display the rotating image
    Image(
        painter = painterResource(id = persona.vectorDrawableId),
        contentDescription = "Rotating processing indicator",
        modifier = Modifier
            .padding(bottom = AppMargin)
            .size(Dimensions.SmallIcon)
            .graphicsLayer(rotationZ = rotation)
    )
}

@PreviewS22Ultra
@Composable
fun Preview() {
    MainScreenPreview()
}
