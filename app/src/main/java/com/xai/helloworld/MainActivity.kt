package com.xai.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xai.helloworld.ui.mainscreen.MainScreen
import com.xai.helloworld.ui.mainscreen.MainScreenPreview
import com.xai.helloworld.ui.mainscreen.PreviewS22Ultra
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(viewModel = viewModel())
        }
    }
}

@PreviewS22Ultra
@Composable
fun Preview() {
    MainScreenPreview()
}