package com.sdp.ssp.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import kotlin.math.sqrt


@Composable
@SuppressLint("ConfigurationScreenWidthHeight")
@Stable
fun getScreenWidthInDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}
@Composable
@SuppressLint("ConfigurationScreenWidthHeight")
@Stable
fun getScreenHeightInDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.dp
}
@Stable
fun getScreenSizeInInches(context: Context): Double {
    val displayMetrics = context.resources.displayMetrics
    val widthInPixels = displayMetrics.widthPixels
    val heightInPixels = displayMetrics.heightPixels
    val screenWidthInInches = widthInPixels / displayMetrics.xdpi
    val screenHeightInInches = heightInPixels / displayMetrics.ydpi

    return sqrt(
        (screenWidthInInches * screenWidthInInches) +
                (screenHeightInInches * screenHeightInInches).toDouble()
    )
}
@Composable
@Stable
fun getScreenSize(): Size {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(context)
        val width = windowMetrics.bounds.width()
        val height = windowMetrics.bounds.height()
        Size(width, height)
    } else {
        val displayMetrics = DisplayMetrics()
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        Size(width, height)
    }
}
@Composable
@Stable
fun getScreenWidth(): Int {
    return getScreenSize().width
}
@Composable
@Stable
fun getScreenHeight(): Int {
    return getScreenSize().height
}
@Composable
@Stable
fun getScreenWidthInPx(): Int {
    val metrics = LocalContext.current.resources.displayMetrics
    return metrics.widthPixels
}
@Composable
@Stable
fun getScreenHeightInPx(): Int {
    val metrics = LocalContext.current.resources.displayMetrics
    return metrics.heightPixels
}
@Composable
@Stable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}
@Composable
@Stable
fun Int.pxToDp(): Dp {
    return with(LocalDensity.current) { this@pxToDp.toDp() }
}
@Composable
@Stable
fun Float.pxToDp(): Dp {
    return with(LocalDensity.current) { this@pxToDp.toDp() }
}

val Context.statusBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

val Context.navigationBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
