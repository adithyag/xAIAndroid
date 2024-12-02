package com.xai.helloworld.ui.mainscreen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.xai.helloworld.R
import com.xai.helloworld.repository.Personas
import com.xai.helloworld.ui.PreviewS22Ultra
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme
import kotlinx.coroutines.flow.MutableStateFlow

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
                type = Type.User,
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
            Message(msg = "I'm fine, thank you!", type = Type.Assistant),
            Message(msg = "Did you not see my images?", type = Type.User),
            Message(msg = "I thought it was jut my shadow.", type = Type.Assistant),
            Message(msg = "Great!", type = Type.User),
            Message(msg = "Client request(POST https://api.x.ai/v1/chat/completions) invalid: 412 Precondition Failed. Text: \"{\"code\":\"The system is not in a state required for the operation's execution\",\"error\":\"Downloaded response does not contain a valid JPG or PNG image.\"}\"", type = Type.Error),
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
    XAIHelloWorldTheme {
        MainScreen(
            messages = messages,
            onUserMessage = onUserMessage,
            images = images,
            processing = MutableStateFlow(true),
            persona = MutableStateFlow(Personas.DEFAULT),
            onPersonaSelected = {},
            onImageSelected = onImageSelected,
            onImageDeleted = onImageDeleted,
            onInfoClick = {},
        )
    }
}
