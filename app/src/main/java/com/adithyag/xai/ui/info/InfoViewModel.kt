package com.adithyag.xai.ui.info

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adithyag.xai.network.XAiApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(val xAiApi: XAiApi) : ViewModel() {

    private val _apiKeyInfo = MutableStateFlow("")
    internal val apiKeyInfo = _apiKeyInfo.asStateFlow()

    private val _models = MutableStateFlow(emptyList<String>())
    internal val models = _models.asStateFlow()

    private val _languageModels = MutableStateFlow(emptyList<String>())
    internal val languageModels = _languageModels.asStateFlow()

    internal fun fetchApiKeyInfo() {
        Log.d("InfoViewModel", "fetchApiKeyInfo")
        _apiKeyInfo.value = "Fetching API Key Info"
        viewModelScope.launch {
            _apiKeyInfo.value = try {
                xAiApi.getApiKeyInfo().toString()
            } catch (e: Exception) {
                e.message ?: e.toString()
            }
        }
    }

    internal fun fetchModels() {
        Log.d("InfoViewModel", "fetchModels")
        _models.value = listOf("Fetching Models")
        viewModelScope.launch {
            _models.value = try {
                xAiApi.getModels().data.map { it.toString() }
            } catch (e: Exception) {
                listOf(e.message ?: e.toString())
            }
        }
    }

    internal fun fetchLanguageModels() {
        Log.d("InfoViewModel", "fetchLanguageModels")
        _languageModels.value = listOf("Fetching LanguageModels")
        viewModelScope.launch {
            _languageModels.value = try {
                xAiApi.getLanguageModels().models.map { it.toString() }
            } catch (e: Exception) {
                listOf(e.message ?: e.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("InfoViewModel", "onCleared")
    }
}