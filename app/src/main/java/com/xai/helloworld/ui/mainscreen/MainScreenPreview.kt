package com.xai.helloworld.ui.mainscreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.xai.helloworld.network.XAiApi
import com.xai.helloworld.network.data.ApiKeyResponse
import com.xai.helloworld.network.data.ChatCompletionsRequest
import com.xai.helloworld.network.data.ChatCompletionsResponse
import com.xai.helloworld.network.data.CompletionsRequest
import com.xai.helloworld.network.data.CompletionsResponse
import com.xai.helloworld.network.data.LanguageModel
import com.xai.helloworld.network.data.LanguageModelsResponse
import com.xai.helloworld.network.data.Model
import com.xai.helloworld.network.data.ModelsResponse
import com.xai.helloworld.network.data.PromptTokensDetails
import com.xai.helloworld.network.data.Usage

private const val DEVICE_SPEC_S22ULTRA = "spec:width=480dp,height=1005dp"

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@Preview(
    device = DEVICE_SPEC_S22ULTRA,
    showSystemUi = true,
)
annotation class PreviewS22Ultra

@PreviewS22Ultra
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
                userId = "",
            )
        }

        override suspend fun getLanguageModels(): LanguageModelsResponse {
            return LanguageModelsResponse(
                models = emptyList(),
            )
        }

        override suspend fun getLanguageModel(modelId: String): LanguageModel {
            return LanguageModel(
                id = "",
                fingerprint = "",
                created = 0,
                objectX = "",
                ownedBy = "",
                version = "",
                inputModalities = emptyList(),
                outputModalities = emptyList(),
                promptTextTokenPrice = 0,
                promptImageTokenPrice = 0,
                completionTextTokenPrice = 0,
            )
        }

        override suspend fun getModels(): ModelsResponse {
            return ModelsResponse(
                data = emptyList(),
                objectX = "",
            )
        }

        override suspend fun getModel(modelId: String): Model {
            return Model(
                id = "",
                created = 0,
                objectX = "",
                ownedBy = "",
            )
        }

        override suspend fun getCompletions(completionRequest: CompletionsRequest)
                : CompletionsResponse {
            return CompletionsResponse(
                id = "",
                completionChoices = emptyList(),
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

        override suspend fun getChatCompletions(chatCompletionRequest: ChatCompletionsRequest)
                : ChatCompletionsResponse {
            return ChatCompletionsResponse(
                id = "",
                objectX = "",
                created = 0,
                model = "",
                choices = emptyList(),
                usage = Usage(
                    completionTokens = 0,
                    promptTokens = 0,
                    totalTokens = 0,
                    promptTokensDetails = PromptTokensDetails(0, 0, 0, 0)
                ),
                systemFingerprint = "",
            )
        }

    }

    val viewModel = MainScreenViewModel(fakeXAiApi).apply {
        onUserMessage("How are you?")
        onModelResponse("I'm fine, thank you!")
        onUserMessage("Great!")
    }
    MainScreen(viewModel)
}
