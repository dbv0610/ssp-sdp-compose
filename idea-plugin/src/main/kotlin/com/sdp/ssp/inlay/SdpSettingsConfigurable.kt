package com.sdp.ssp.inlay

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class SdpSettingsConfigurable : Configurable {

    private var widthField: JBTextField? = null

    override fun getDisplayName(): String = "SDP/SSP Hints"

    override fun createComponent(): JComponent {
        val field = JBTextField(SdpSettings.getInstance().deviceWidthDp.toString(), 8)
        widthField = field
        val row = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("Device smallest width (dp) for computed values:"))
            add(field)
        }
        return JPanel(BorderLayout()).apply { add(row, BorderLayout.NORTH) }
    }

    override fun isModified(): Boolean =
        widthField?.text?.trim()?.toIntOrNull() != SdpSettings.getInstance().deviceWidthDp

    override fun apply() {
        val value = widthField?.text?.trim()?.toIntOrNull() ?: return
        if (value > 0) SdpSettings.getInstance().deviceWidthDp = value
    }

    override fun reset() {
        widthField?.text = SdpSettings.getInstance().deviceWidthDp.toString()
    }

    override fun disposeUIResources() {
        widthField = null
    }
}
