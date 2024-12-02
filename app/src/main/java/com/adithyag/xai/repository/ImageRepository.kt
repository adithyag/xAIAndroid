package com.adithyag.xai.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
class ImageRepository @Inject constructor(@ApplicationContext val context: Context) {

    private val thumbnailSize = Size(200, 200)

    suspend fun getThumbnailFromUri(uri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            context.contentResolver.loadThumbnail(uri, this@ImageRepository.thumbnailSize, null)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getBase64EncodedData(imageUri: Uri): String = withContext(Dispatchers.IO) {
        Base64.encode(context.contentResolver.openInputStream(imageUri)?.use { it.readAllBytes() }
            ?: throw Exception("Failed to read image data"))
    }

    suspend fun getMimeType(uri: Uri): String? = withContext(Dispatchers.IO) {
        context.contentResolver.getType(uri).also { Log.d("ImageRepository", "getMimeType: $it") }
    }
}