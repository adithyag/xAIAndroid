package com.xai.helloworld

import android.os.Bundle
import android.util.Log
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
import com.xai.helloworld.network.createXAiApi
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var messages: List<Message> by mutableStateOf(emptyList())
    private val xAiApi by lazy {
        Ktorfit.Builder()
            .baseUrl("https://api.x.ai/v1/")
            .httpClient(Android) {
                install(UserAgent) {
                    agent = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}/" +
                            "${BuildConfig.VERSION_CODE}"
                }
                install(ContentNegotiation) {
                    json()
                }
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(BuildConfig.API_KEY, null)
                        }
                    }
                }
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.d("Ktor", message)
                        }
                    }
                    sanitizeHeader { header ->
                        header == "Authorization"
                    }
                }
            }
            .build()
            .createXAiApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CoroutineScope(Dispatchers.IO).launch {
            val apiKeyInfo = xAiApi.getApiKeyInfo()
            withContext(Dispatchers.Main) {
                messages += Message(apiKeyInfo.toString())
            }
        }
        setContent {
            MainScreen(messages) {
                messages += Message(it)
            }
        }
    }
}

@Composable
private fun MainScreen(
    messages: List<Message>,
    onUserMessage: (String) -> Unit,
) {
    XAIHelloWorldTheme {
        Column(
            modifier = Modifier
                .background(Color.Yellow)
                .safeDrawingPadding()
        ) {
            LazyColumn(
                Modifier
                    .background(Color.Magenta)
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true,
            ) {
                items(messages.asReversed(), key = Message::id) {
                    ChatMessage(it)
                }
            }
            ChatInput(onUserMessage)
        }
    }
}

@Composable
private fun ChatMessage(message: Message) {
    Text(
        modifier = Modifier
            .background(
                when (message.role) {
                    Role.System -> Color.Blue
                    Role.Assistant -> Color.Red
                    Role.User -> Color.Green
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
    val messages = listOf(
        Message("Hello World"),
        Message("How are you?", Role.Assistant),
        Message("I'm fine, thank you!"),
    )
    MainScreen(messages) {}
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