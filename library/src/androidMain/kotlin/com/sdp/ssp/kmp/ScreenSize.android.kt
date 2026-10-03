package com.sdp.ssp.kmp

import androidx.compose.runtime.Composable

@Composable
internal actual fun platformScreenWidth(): Float =
    SdpRuntime.configuration.screenWidthDp.toFloat()

@Composable
internal actual fun platformScreenHeight(): Float =
    SdpRuntime.configuration.screenHeightDp.toFloat()
