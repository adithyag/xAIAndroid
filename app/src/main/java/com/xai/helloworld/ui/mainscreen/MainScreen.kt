package com.xai.helloworld.ui.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme

@Composable
internal fun MainScreen(viewModel: MainScreenViewModel) {
    XAIHelloWorldTheme {
        Column(
            modifier = Modifier.Companion
                .background(Color.Companion.Yellow)
                .safeDrawingPadding()
        ) {
            val messages by viewModel.messages.collectAsState()
            LazyColumn(
                Modifier.Companion
                    .background(Color.Companion.Magenta)
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
    Text(
        modifier = Modifier.Companion
            .background(
                when (message.role) {
                    Role.Assistant -> Color.Companion.Cyan
                    Role.User -> {
                        if (message.pending) Color.Companion.Gray
                        else Color.Companion.Yellow
                    }
                }
            )
            .fillMaxWidth(),
        text = message.toString()
    )
}

@Composable
private fun ChatInput(onUserMessage: (String) -> Unit) {
    var currentUserInput by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(Color.Companion.Green)
    ) {
        TextField(
            modifier = Modifier.Companion
                .background(Color.Companion.Transparent)
                .weight(1f),
            value = currentUserInput,
            onValueChange = { currentUserInput = it }
        )
        FilledIconButton(onClick = {
            onUserMessage(currentUserInput)
            currentUserInput = ""
        }) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send Question",
            )
        }
    }
}

@Preview(showBackground = true, device = DEVICE_SPEC_S22ULTRA)
@Composable
fun Preview() {
    MainScreenPreview()
}
