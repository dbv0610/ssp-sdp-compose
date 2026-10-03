package com.sdp.ssp.inlay

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

/**
 * The device width (in dp) the hints are computed for. 420 is the typical
 * phone; set your own device's smallest width to see its exact values.
 */
@Service(Service.Level.APP)
@State(name = "SdpSspValueHints", storages = [Storage("sdpSspValueHints.xml")])
class SdpSettings : PersistentStateComponent<SdpSettings.State> {

    class State {
        var deviceWidthDp: Int = 420
    }

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var deviceWidthDp: Int
        get() = state.deviceWidthDp
        set(value) {
            if (value > 0) state.deviceWidthDp = value
        }

    companion object {
        fun getInstance(): SdpSettings = service()
    }
}
