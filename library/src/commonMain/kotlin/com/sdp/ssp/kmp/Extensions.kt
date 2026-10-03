package com.sdp.ssp.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Int.sdp: Dp @Composable get() = scaledSdp()
val Float.sdp: Dp @Composable get() = scaledSdp()
val Double.sdp: Dp @Composable get() = scaledSdp()

val Int.ssp: TextUnit @Composable get() = scaledSsp()
val Float.ssp: TextUnit @Composable get() = scaledSsp()
val Double.ssp: TextUnit @Composable get() = scaledSsp()

@Composable
private fun <T : Number> T.scaledSdp(): Dp = (toDouble() * screenScale()).dp

@Composable
private fun <T : Number> T.scaledSsp(): TextUnit = (toDouble() * screenScale()).toFloat().sp

@Composable
private fun screenScale(): Double =
    SDPConfig.scaleFactor(minOf(platformScreenWidth(), platformScreenHeight()).toDouble())

/** Width of the screen (Android) or window (other platforms) in dp. */
@Composable
internal expect fun platformScreenWidth(): Float

/** Height of the screen (Android) or window (other platforms) in dp. */
@Composable
internal expect fun platformScreenHeight(): Float
