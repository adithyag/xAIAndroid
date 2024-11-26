package com.xai.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.xai.helloworld.ui.mainscreen.MainScreen
import com.xai.helloworld.ui.mainscreen.MainScreenViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(viewModel = viewModels<MainScreenViewModel>().value)
        }
    }
}

// Samsung S22 Ultra is 480x1005dp
@Preview(showBackground = true, device = "spec:width=480dp,height=1005dp")
@Composable
fun MainScreenPreview() {
    val viewModel = MainScreenViewModel().apply {
        onUserMessage("Hello World")
        onUserMessage("How are you?")
        onUserMessage("I'm fine, thank you!")
    }
    MainScreen(viewModel)
}