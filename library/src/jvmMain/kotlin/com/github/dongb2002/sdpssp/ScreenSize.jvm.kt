package com.github.dongb2002.sdpssp

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun getScreenWidth(): Float {
    val density = LocalDensity.current
    return with(density) { LocalWindowInfo.current.containerSize.width.toDp().value }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun getScreenHeight(): Float {
    val density = LocalDensity.current
    return with(density) { LocalWindowInfo.current.containerSize.height.toDp().value }
}
