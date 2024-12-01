package com.xai.helloworld.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0XFF192328),
    onPrimary = Color(0XFFFFFFFF),
    secondary = Color(0xFF0F1518),
    onSecondary = Color(0XFFFFFFFF),
    tertiary = Color(0xFF2196F3),
    background = Color(0xFF050505),
    inverseSurface = Color(0xFF2F312D),
    inverseOnSurface = Color(0xFF8D9286),
)

@Composable
fun XAIHelloWorldTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}