package com.sdp.ssp.android

import android.annotation.SuppressLint
import android.content.Context
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import kotlin.math.sqrt

/** Current screen width as [Dp], observed from the composition's configuration. */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenWidthInDp(): Dp = LocalConfiguration.current.screenWidthDp.dp

/** Current screen height as [Dp], observed from the composition's configuration. */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenHeightInDp(): Dp = LocalConfiguration.current.screenHeightDp.dp

/**
 * Size of the current window in pixels, including system bars (via
 * [WindowMetricsCalculator], correct in multi-window / split screen).
 */
@Composable
fun getScreenSize(): Size {
    val bounds = WindowMetricsCalculator.getOrCreate()
        .computeCurrentWindowMetrics(LocalContext.current)
        .bounds
    return Size(bounds.width(), bounds.height())
}

/** Width of the current window in pixels, including system bars ([getScreenSize]`.width`). */
@Composable
fun getScreenWidth(): Int = getScreenSize().width

/** Height of the current window in pixels, including system bars ([getScreenSize]`.height`). */
@Composable
fun getScreenHeight(): Int = getScreenSize().height

/** Width of the display area in pixels, from the composition's resources. */
@Composable
fun getScreenWidthInPx(): Int = LocalContext.current.resources.displayMetrics.widthPixels

/** Height of the display area in pixels, from the composition's resources. */
@Composable
fun getScreenHeightInPx(): Int = LocalContext.current.resources.displayMetrics.heightPixels

/** Physical diagonal size of the screen in inches. */
fun getScreenSizeInInches(context: Context): Double {
    val displayMetrics = context.resources.displayMetrics
    val widthInInches = displayMetrics.widthPixels / displayMetrics.xdpi
    val heightInInches = displayMetrics.heightPixels / displayMetrics.ydpi
    return sqrt((widthInInches * widthInInches + heightInInches * heightInInches).toDouble())
}

// ── Density conversions ──────────────────────────────────────────────────────

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
 * [percent] percent of the screen width as [Dp], e.g. `widthPercentDp(50f)`
 * is half the width.
 */
@Composable
fun widthPercentDp(percent: Float): Dp =
    (SdpRuntime.configuration.screenWidthDp * percent / 100f).dp

/**
 * [percent] percent of the screen height as [Dp], e.g. `heightPercentDp(50f)`
 * is half the height.
 */
@Composable
fun heightPercentDp(percent: Float): Dp =
    (SdpRuntime.configuration.screenHeightDp * percent / 100f).dp

// ── Context-based versions (usable outside composition) ─────────────────────

/** Screen width as [Dp] from this context's configuration. */
fun Context.getScreenWidthInDp(): Dp = resources.configuration.screenWidthDp.dp

/** Screen height as [Dp] from this context's configuration. */
fun Context.getScreenHeightInDp(): Dp = resources.configuration.screenHeightDp.dp

/** Width of the display area in pixels from this context's resources. */
fun Context.getScreenWidthInPx(): Int = resources.displayMetrics.widthPixels

/** Height of the display area in pixels from this context's resources. */
fun Context.getScreenHeightInPx(): Int = resources.displayMetrics.heightPixels

// ── System insets (legacy resource lookup) ──────────────────────────────────

/** Height of the status bar in pixels, from the platform dimen resource. */
val Context.statusBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

/** Height of the navigation bar in pixels, from the platform dimen resource. */
val Context.navigationBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
