package com.sdp.ssp.kmp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
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
fun getScreenWidthInPx(): Int = LocalResources.current.displayMetrics.widthPixels

/** Height of the display area in pixels, from the composition's resources. */
@Composable
fun getScreenHeightInPx(): Int = LocalResources.current.displayMetrics.heightPixels

/** Physical diagonal size of the screen in inches. */
fun getScreenSizeInInches(context: Context): Double {
    val displayMetrics = context.resources.displayMetrics
    val widthInInches = displayMetrics.widthPixels / displayMetrics.xdpi
    val heightInInches = displayMetrics.heightPixels / displayMetrics.ydpi
    return sqrt((widthInInches * widthInInches + heightInInches * heightInInches).toDouble())
}

// ── Context-based versions (usable outside composition) ─────────────────────

/** Screen width as [Dp] from this context's configuration. */
fun Context.getScreenWidthInDp(): Dp = resources.configuration.screenWidthDp.dp

/** Screen height as [Dp] from this context's configuration. */
fun Context.getScreenHeightInDp(): Dp = resources.configuration.screenHeightDp.dp

/** Width of the display area in pixels from this context's resources. */
fun Context.getScreenWidthInPx(): Int = resources.displayMetrics.widthPixels

/** Height of the display area in pixels from this context's resources. */
fun Context.getScreenHeightInPx(): Int = resources.displayMetrics.heightPixels
