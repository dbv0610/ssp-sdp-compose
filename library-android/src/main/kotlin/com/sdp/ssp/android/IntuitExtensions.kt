package com.sdp.ssp.android

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val BASE_DP = 300f
private const val MIN_SW_DP = 300
private const val MAX_SW_DP = 1080
private const val SW_STEP_DP = 30

/**
 * Scale factor for a given smallest screen width, replicating the old
 * `values-swXXXdp` resource buckets: the width is floored to a 30dp step and
 * clamped to `300..1080`; below 300dp the scale is 1 (1sdp = 1dp).
 */
private fun scaleFactor(smallestScreenWidthDp: Int): Float {
    if (smallestScreenWidthDp < MIN_SW_DP) return 1f
    val bucket = minOf(smallestScreenWidthDp - smallestScreenWidthDp % SW_STEP_DP, MAX_SW_DP)
    return bucket / BASE_DP
}

private val deviceScale: Float
    get() = scaleFactor(SdpRuntime.configuration.smallestScreenWidthDp)

private val deviceFontScale: Float
    get() = SdpRuntime.configuration.fontScale

/**
 * **Scalable dp** — a [Dp] that scales with the device's smallest screen width.
 *
 * Actual size = `value × smallestScreenWidth / 300` dp:
 *
 * | Smallest width          | Scale |
 * |-------------------------|-------|
 * | 300dp (small phone)     | ×1.0  |
 * | 360dp (typical phone)   | ×1.2  |
 * | 390dp (large phone)     | ×1.3  |
 * | 480dp (small tablet)    | ×1.6  |
 * | 600dp (7" tablet)       | ×2.0  |
 *
 * Values are identical to the `intuit/sdp` library (same 30dp buckets, same
 * `n × sw / 300` formula), but computed in pure Kotlin from the app configuration
 * instead of dimen resources. Not composable, so it can be
 * used anywhere: default parameter values, plain classes, drawing code, etc.
 * The value is read at call time; it is not observed by composition, so a
 * configuration change only takes effect on the next read (activity recreation
 * covers the common cases).
 *
 * @see Ssp for text sizes
 * @see getSdp for a window-aware version (multi-window / split screen)
 */
@get:Stable
val Int.Sdp: Dp
    get() = (this * deviceScale).dp

/**
 * **Scalable sp** — a text size [TextUnit] that scales with the device's smallest
 * screen width but **ignores the user's system font-size setting**.
 *
 * Actual size = `value × smallestScreenWidth / 300` sp:
 *
 * | Smallest width          | Scale |
 * |-------------------------|-------|
 * | 300dp (small phone)     | ×1.0  |
 * | 360dp (typical phone)   | ×1.2  |
 * | 390dp (large phone)     | ×1.3  |
 * | 480dp (small tablet)    | ×1.6  |
 * | 600dp (7" tablet)       | ×2.0  |
 *
 * Not composable — usable anywhere. The user's font scale is divided out at call
 * time so the rendered size stays fixed regardless of the accessibility setting.
 *
 * @see RSsp if the text should also respect the user's font-size (accessibility) setting
 */
@get:Stable
val Int.Ssp: TextUnit
    get() = (this * deviceScale / deviceFontScale).sp

/**
 * **Respectful scalable sp** — like [Ssp], scales with the device's smallest screen
 * width, but **also respects the user's system font-size (accessibility) setting**.
 *
 * Base size = `value × smallestScreenWidth / 300` sp, then multiplied by the user's
 * font scale at render time (e.g. `12.RSsp` on a 360dp-wide phone ≈ 14.4sp × font scale).
 *
 * This matches the behavior of the `intuit/ssp` library (its dimens are declared
 * in `sp`, so they scale with the accessibility setting too).
 */
@get:Stable
val Int.RSsp: TextUnit
    get() = (this * deviceScale).sp


// ── Context-based versions (window-aware) ────────────────────────────────────

/**
 * Window-aware version of [Sdp]: returns the scaled size **in dp** as a [Float],
 * using the given [context]'s configuration (correct in multi-window / split screen).
 *
 * Actual size = `value × smallestScreenWidth / 300` dp (e.g. `12.getSdp(ctx)` on a
 * 360dp-wide phone returns `14.4`).
 */
fun Int.getSdp(context: Context): Float =
    this * scaleFactor(context.resources.configuration.smallestScreenWidthDp)

/**
 * Window-aware version of [RSsp]: returns the scaled text size **in sp** as a [Float],
 * using the given [context]'s configuration (correct in multi-window / split screen).
 *
 * Actual size = `value × smallestScreenWidth / 300` sp (e.g. `12.getSsp(ctx)` on a
 * 360dp-wide phone returns `14.4`).
 */
fun Int.getSsp(context: Context): Float =
    this * scaleFactor(context.resources.configuration.smallestScreenWidthDp)
