package com.sdp.ssp.kmp

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalConfiguration

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
internal actual fun getScreenWidth(): Float = LocalConfiguration.current.screenWidthDp.toFloat()

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
internal actual fun getScreenHeight(): Float = LocalConfiguration.current.screenHeightDp.toFloat()
