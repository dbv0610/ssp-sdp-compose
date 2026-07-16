package com.sdp.ssp.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun platformScreenWidth(): Float {
    val density = LocalDensity.current
    return with(density) { LocalWindowInfo.current.containerSize.width.toDp().value }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun platformScreenHeight(): Float {
    val density = LocalDensity.current
    return with(density) { LocalWindowInfo.current.containerSize.height.toDp().value }
}
