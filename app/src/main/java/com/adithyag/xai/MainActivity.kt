package com.adithyag.xai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adithyag.xai.ui.mainscreen.MainScreen
import com.adithyag.xai.ui.mainscreen.MainScreenPreview
import com.adithyag.xai.ui.mainscreen.MainScreenViewModel
import com.adithyag.xai.ui.PreviewS22Ultra
import com.adithyag.xai.ui.info.InfoScreen
import com.adithyag.xai.ui.theme.XAiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            XAiTheme {
                NavHost(navController = navController, startDestination = MainScreenDestination) {
                    composable<MainScreenDestination> {
                        MainScreen(viewModel = hiltViewModel<MainScreenViewModel>()) {
                            navController.navigate(InfoScreenDestination)
                        }
                    }
                    composable<InfoScreenDestination> {
                        InfoScreen(viewModel = hiltViewModel()) {
                            navController.navigateUp()
                        }
                    }
                }
            }
        }
    }
}

@Serializable
object MainScreenDestination

@Serializable
object InfoScreenDestination

@PreviewS22Ultra
@Composable
fun Preview() {
    MainScreenPreview()
}