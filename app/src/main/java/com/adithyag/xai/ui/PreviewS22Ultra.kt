package com.adithyag.xai.ui

import androidx.compose.ui.tooling.preview.Preview

private const val DEVICE_SPEC_S22ULTRA = "spec:width=480dp,height=1005dp"

@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@Preview(
    device = DEVICE_SPEC_S22ULTRA,
    showSystemUi = true,
)
annotation class PreviewS22Ultra