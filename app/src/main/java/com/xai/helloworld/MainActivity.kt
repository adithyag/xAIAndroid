package com.xai.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.xai.helloworld.network.getXAiApi
import com.xai.helloworld.ui.MainScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var messages: List<Message> by mutableStateOf(emptyList())
    private val xAiApi by lazy { getXAiApi() }

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