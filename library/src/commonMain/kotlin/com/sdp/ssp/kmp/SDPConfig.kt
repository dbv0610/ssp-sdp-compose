package com.sdp.ssp.kmp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SDPConfig {
    private var baseRatio by mutableStateOf(360.0)

    fun setScalingRatio(ratio: Double) {
        if (ratio > 0) baseRatio = ratio
    }

    internal fun getScalingRatio(): Double = baseRatio
}
