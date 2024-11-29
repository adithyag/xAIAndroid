package com.xai.helloworld.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xai.helloworld.R
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme

@Composable
internal fun MainScreen(viewModel: MainScreenViewModel) {
    XAIHelloWorldTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .paint(
                    painterResource(R.drawable.grok_text),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
                .safeDrawingPadding(),
        ) {
            val messages by viewModel.messages.collectAsState()
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true,
            ) {
                items(messages.asReversed()) {
                    ChatMessage(it)
                }
            }
            ChatInput(viewModel::onUserMessage)
        }
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
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp)
                .background(
                    when (message.role) {
                        Role.Assistant -> MaterialTheme.colorScheme.primary
                        Role.User -> {
                            if (message.pending) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.background
                        }
                    },
                    shape = MaterialTheme.shapes.small
                )
                .padding(4.dp)
                .fillMaxWidth(),
            text = message.msg,
            style = MaterialTheme.typography.bodyLarge,
            color = when (message.role) {
                Role.Assistant -> MaterialTheme.colorScheme.onSecondaryContainer
                Role.User -> {
                    if (message.pending) MaterialTheme.colorScheme.onTertiary
                    else MaterialTheme.colorScheme.onPrimaryContainer
                }

            }
        )
    }
}

@Composable
private fun ChatInput(onUserMessage: (String) -> Unit) {
    var currentUserInput by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            placeholder = @Composable {
                Text(
                    text = "Ask xAI anything",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            value = currentUserInput,
            trailingIcon = @Composable {
                SendIcon(currentUserInput.isNotBlank()) {
                    onUserMessage(currentUserInput)
                    currentUserInput = ""
                }
            },
            onValueChange = { currentUserInput = it },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text,
            )
        )

    }
}

@Composable
fun SendIcon(enabled: Boolean, onClick: () -> Unit) {
    Image(
        ImageVector.vectorResource(R.drawable.send),
        contentDescription = "Send message to Grok",
        Modifier
            .size(36.dp)
            .clickable(enabled = enabled, onClick = onClick),
        colorFilter = ColorFilter.tint(
            if (enabled)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.inverseSurface
        )
    )
}

@PreviewS22Ultra
@Composable
fun Preview() {
    MainScreenPreview()
}
