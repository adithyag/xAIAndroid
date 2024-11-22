package com.xai.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    XAIHelloWorldTheme {

        Column(
            modifier = Modifier
                .background(Color.Yellow)
                .safeDrawingPadding()
        ) {
            var messages: List<Message> by rememberSaveable { mutableStateOf(emptyList()) }

            LazyColumn(
                Modifier
                    .background(Color.Magenta)
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true
            ) {
                items(messages, key = Message::id) {
                    ChatMessage(it)
                }
            }

            ChatInput { message ->
                messages = messages + Message(message)
            }
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
    val pending: Boolean = true
) {
    companion object {
        var id = 0L
    }
}

@Composable
private fun ChatMessage(message: Message) {
    Text(message.toString())
}

@Composable
private fun ChatInput(onUserMessage: (String) -> Unit) {
    var currentUserInput by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green)
    ) {
        TextField(
            modifier = Modifier
                .background(Color.Transparent)
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

// Samsung S22 Ultra is 480x1005dp
@Preview(showBackground = true, device = "spec:width=480dp,height=1005dp")
@Composable
fun MainScreenPreview() {
    MainScreen()
}