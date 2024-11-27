package com.xai.helloworld.ui.mainscreen

import androidx.compose.runtime.Composable
import com.xai.helloworld.network.XAiApi
import com.xai.helloworld.network.data.ApiKeyResponse
import com.xai.helloworld.network.data.CompletionsRequest
import com.xai.helloworld.network.data.CompletionsResponse
import com.xai.helloworld.network.data.PromptTokensDetails
import com.xai.helloworld.network.data.Usage

// Samsung S22 Ultra is 480x1005dp

internal const val DEVICE_SPEC_S22ULTRA = "spec:width=480dp,height=1005dp"

@Composable
fun MainScreenPreview() {
    val fakeXAiApi = object : XAiApi {
        override suspend fun getApiKeyInfo(): ApiKeyResponse {
            return ApiKeyResponse(
                acls = emptyList(),
                apiKeyBlocked = false,
                apiKeyDisabled = false,
                apiKeyId = "",
                createTime = "",
                modifiedBy = "",
                modifyTime = "",
                name = "",
                redactedApiKey = "",
                teamBlocked = false,
                teamId = "",
                userId = ""
            )
        }

        override suspend fun getCompletions(completionRequest: CompletionsRequest): CompletionsResponse {
            return CompletionsResponse(
                id = "",
                choices = emptyList(),
                created = 0,
                model = "",
                systemFingerprint = "",
                objectX = "",
                usage = Usage(
                    completionTokens = 0,
                    promptTokens = 0,
                    totalTokens = 0,
                    promptTokensDetails = PromptTokensDetails(0, 0, 0, 0)
                )
            )
        }

    }

    val viewModel = MainScreenViewModel(fakeXAiApi).apply {
        onUserMessage("Hello World")
        onUserMessage("How are you?")
        onUserMessage("I'm fine, thank you!")
    }
    MainScreen(viewModel)
}
