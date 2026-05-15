package com.github.dongb2002.sdpssp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

val Int.sdp: Dp @Composable get() = scaledSdp()
val Float.sdp: Dp @Composable get() = scaledSdp()
val Double.sdp: Dp @Composable get() = scaledSdp()

val Int.ssp: TextUnit @Composable get() = scaledSsp()
val Float.ssp: TextUnit @Composable get() = scaledSsp()
val Double.ssp: TextUnit @Composable get() = scaledSsp()

@Composable
private fun <T : Number> T.scaledSdp(): Dp {
    val minValue = minOf(getScreenWidth(), getScreenHeight()) / SDPConfig.getScalingRatio()
    return (toDouble() * minValue).dp
}

@Composable
private fun <T : Number> T.scaledSsp(): TextUnit {
    val density = LocalDensity.current
    return scaledSdp().toSp(density)
}

private fun Dp.toSp(density: Density): TextUnit = with(density) { toSp() }

@Composable
internal expect fun getScreenWidth(): Float

@Composable
internal expect fun getScreenHeight(): Float
