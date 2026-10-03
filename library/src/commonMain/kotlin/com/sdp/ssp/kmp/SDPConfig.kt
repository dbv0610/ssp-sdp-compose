package com.sdp.ssp.kmp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global scaling settings for `sdp` / `ssp`.
 *
 * Sizes follow a three-segment curve over the screen's smallest side (`sw`):
 *
 * | Segment                        | Scaling                                        |
 * |--------------------------------|------------------------------------------------|
 * | `sw < base` (small phones)     | shrinks at [setSmallScreenRate] (default 1)    |
 * | `base ≤ sw ≤ breakpoint`       | linear with the screen                         |
 * | `sw > breakpoint` (tablets…)   | grows at [setLargeScreenRate] (default 1)      |
 *
 * With the default rates of `1.0` sizes are exactly proportional to the screen, so the
 * layout keeps the same proportions on every device. Lower a rate (`0.0` stops scaling)
 * only if you want, e.g., tablets to show more content instead of bigger content.
 */
object SDPConfig {
    private var baseRatio by mutableStateOf(360.0)
    private var breakpointDp by mutableStateOf(480.0)
    private var shrinkRate by mutableStateOf(1.0)
    private var growRate by mutableStateOf(1.0)

    /** Screen width in dp the design was made for (`sdp` / `ssp` only). Default 360. */
    fun setScalingRatio(ratio: Double) {
        if (ratio > 0) baseRatio = ratio
    }

    /** Smallest width in dp above which growth is damped. Default 480. */
    fun setLargeScreenBreakpoint(dp: Double) {
        if (dp > 0) breakpointDp = dp
    }

    /** How fast sizes shrink below the base width, `0.0..1.0`. Default 1.0. */
    fun setSmallScreenRate(rate: Double) {
        if (rate in 0.0..1.0) shrinkRate = rate
    }

    /** How fast sizes grow above the breakpoint, `0.0..1.0`. Default 1.0. */
    fun setLargeScreenRate(rate: Double) {
        if (rate in 0.0..1.0) growRate = rate
    }

    internal fun getScalingRatio(): Double = baseRatio

    /** Scale factor for a screen whose smallest side is [screenDp], relative to [baseDp]. */
    internal fun scaleFactor(screenDp: Double, baseDp: Double = baseRatio): Double {
        val breakpoint = maxOf(breakpointDp, baseDp)
        return when {
            screenDp < baseDp -> 1.0 - (1.0 - screenDp / baseDp) * shrinkRate
            screenDp <= breakpoint -> screenDp / baseDp
            else -> (breakpoint + (screenDp - breakpoint) * growRate) / baseDp
        }
    }
}
