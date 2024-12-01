package com.xai.helloworld.ui.mainscreen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.xai.helloworld.R
import com.xai.helloworld.repository.LlmDomain.Role
import kotlinx.coroutines.flow.MutableStateFlow

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
    val drawable = R.drawable.ic_launcher_foreground
    val painter = ContextCompat.getDrawable(LocalContext.current, drawable)?.let {
        val bitmap =
            Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        it.setBounds(0, 0, canvas.width, canvas.height)
        it.draw(canvas)
        BitmapPainter(bitmap.asImageBitmap())
    }
    val messages = MutableStateFlow<List<Message>>(
        listOf(
            Message(
                msg = "How are you?",
                role = Role.User,
                images = listOf(
                    Image(
                        uri = Uri.parse("android.resource://com.xai.helloworld/" + R.drawable.ic_launcher_foreground),
                        thumbnailPainter = painter!!,
                        mimeType = "image/jpeg",
                        base64 = "bleh"
                    ),
                    Image(
                        uri = Uri.parse("android.resource://com.xai.helloworld/" + R.drawable.ic_launcher_foreground),
                        thumbnailPainter = painter!!,
                        mimeType = "image/jpeg",
                        base64 = "bleh"
                    ),
                    Image(
                        uri = Uri.parse("android.resource://com.xai.helloworld/" + R.drawable.ic_launcher_foreground),
                        thumbnailPainter = painter!!,
                        mimeType = "image/jpeg",
                        base64 = "bleh"
                    ),
                )
            ),
            Message(msg = "I'm fine, thank you!", role = Role.Assistant),
            Message(msg = "Great!", role = Role.User, pending = true),
        )
    )
    val onUserMessage: (String) -> Unit = {}
    val images = MutableStateFlow<List<Image>>(
        listOf(
            Image(
                uri = Uri.parse("android.resource://com.xai.helloworld/" + R.drawable.ic_launcher_foreground),
                thumbnailPainter = painter!!,
                mimeType = "image/jpeg",
                base64 = "bleh"
            )
        )
    )
    val onImageDeleted: (Image) -> Unit = { images.value -= it }
    val onImageSelected: (Uri?) -> Unit = {}
    MainScreen(
        messages = messages,
        onUserMessage = onUserMessage,
        images = images,
        onImageSelected = onImageSelected,
        onImageDeleted = onImageDeleted,
    )
}
