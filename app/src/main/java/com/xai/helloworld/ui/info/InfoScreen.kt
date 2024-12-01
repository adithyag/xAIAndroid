package com.xai.helloworld.ui.info

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.xai.helloworld.ui.PreviewS22Ultra
import com.xai.helloworld.ui.theme.Dimensions
import com.xai.helloworld.ui.theme.Dimensions.AppMargin
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InfoScreen(viewModel: InfoViewModel, onDone: () -> Unit) {
    InfoScreen(
        viewModel.apiKeyInfo,
        viewModel::fetchApiKeyInfo,
        viewModel.models,
        viewModel::fetchModels,
        viewModel.languageModels,
        viewModel::fetchLanguageModels,
        onDone
    )
}

@Composable
fun InfoScreen(
    apiKeyInfo: StateFlow<String> = MutableStateFlow(""),
    onFetchApiKeyInfo: () -> Unit = {},
    models: StateFlow<List<String>> = MutableStateFlow(emptyList()),
    onFetchModels: () -> Unit = {},
    languageModels: StateFlow<List<String>> = MutableStateFlow(emptyList()),
    onFetchLanguageModels: () -> Unit = {},
    onDone: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize()
            .padding(AppMargin)
            .clickable {
                onDone()
            }
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.spacedBy(AppMargin)
    ) {
        item { InfoCard("API Key Info", apiKeyInfo, onFetchApiKeyInfo) }
        item { ListInfoCard("Models", models, onFetchModels) }
        item { ListInfoCard("Language Models", languageModels, onFetchLanguageModels) }
    }
}

@Composable
fun InfoCard(title: String, contentFlow: StateFlow<String>, onClick: () -> Unit) {
    Card(
        onClick = {
            Log.d("InfoScreen", "$title Card clicked")
            onClick()
        },
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
    ) {
        TitleText(title)
        SubtitleText()

        val content = contentFlow.collectAsState()
        ContentText(content.value)
    }
}

@Composable
fun ListInfoCard(title: String, contentFlow: StateFlow<List<String>>, onClick: () -> Unit) {
    Card(
        onClick = {
            Log.d("InfoScreen", "$title Card clicked")
            onClick()
        },
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
    ) {
        TitleText(title)
        SubtitleText(text = "Click to refresh. Click on each item to see more details")

        val content = contentFlow.collectAsState()
        for (item in content.value) {
            ContentText(
                item
            )
        }
    }
}

@Composable
private fun TitleText(text: String) {
    Text(
        text = text,
        Modifier.padding(top = Dimensions.BadgePadding, start = Dimensions.BadgePadding),
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
private fun SubtitleText(text: String = "Click to refresh") {
    Text(
        text = text,
        Modifier.padding(start = Dimensions.BadgePadding, bottom = Dimensions.BadgePadding),
        style = MaterialTheme.typography.titleSmall,
        color = colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ContentText(text: String) {
    Text(
        text = text,
        Modifier
            .padding(start = Dimensions.BadgePadding, bottom = Dimensions.BadgePadding)
            .clickable {

            },
        style = MaterialTheme.typography.bodyLarge,
    )
}

@PreviewS22Ultra
@Composable
fun InfoScreenPreview() {
    XAIHelloWorldTheme {
        InfoScreen(
            apiKeyInfo = MutableStateFlow("API Key is good!"),
            models = MutableStateFlow(listOf("Model 1", "Model 2")),
            languageModels = MutableStateFlow(listOf("Language Model 1", "Language Model 2"))
        )
    }
}
