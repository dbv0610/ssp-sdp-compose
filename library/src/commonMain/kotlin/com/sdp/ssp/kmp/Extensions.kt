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
private fun <T : Number> T.scaledSdp(): Dp {
    val minValue = minOf(platformScreenWidth(), platformScreenHeight()) / SDPConfig.getScalingRatio()
    return (toDouble() * minValue).dp
}

@Composable
private fun <T : Number> T.scaledSsp(): TextUnit {
    val minValue = minOf(platformScreenWidth(), platformScreenHeight()) / SDPConfig.getScalingRatio()
    return (toDouble() * minValue).toFloat().sp
}

/** Width of the screen (Android) or window (other platforms) in dp. */
@Composable
internal expect fun platformScreenWidth(): Float

/** Height of the screen (Android) or window (other platforms) in dp. */
@Composable
internal expect fun platformScreenHeight(): Float
