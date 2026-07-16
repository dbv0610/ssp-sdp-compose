package com.sdp.ssp.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Converts this [Dp] to pixels using the current composition's density. */
@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) { this@toPx.toPx() }

/** Converts this [Dp] to pixels using the given [density]. Usable outside composition. */
fun Dp.toPx(density: Density): Float = with(density) { this@toPx.toPx() }

/** Converts this pixel value to [Dp] using the current composition's density. */
@Composable
fun Int.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }

/** Converts this pixel value to [Dp] using the current composition's density. */
@Composable
fun Float.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }

/**
 * [percent] percent of the screen width (Android) or window width (other
 * platforms) as [Dp], e.g. `widthPercentDp(50f)` is half the width.
 */
@Composable
fun widthPercentDp(percent: Float): Dp = (platformScreenWidth() * percent / 100f).dp

/**
 * [percent] percent of the screen height (Android) or window height (other
 * platforms) as [Dp], e.g. `heightPercentDp(50f)` is half the height.
 */
@Composable
fun heightPercentDp(percent: Float): Dp = (platformScreenHeight() * percent / 100f).dp
