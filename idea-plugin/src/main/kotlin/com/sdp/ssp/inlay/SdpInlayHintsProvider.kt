    package com.sdp.ssp.inlay

import com.intellij.codeInsight.hints.declarative.HintFormat
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider
import com.intellij.codeInsight.hints.declarative.InlayTreeSink
import com.intellij.codeInsight.hints.declarative.InlineInlayPosition
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import kotlin.math.floor

/**
 * Shows the computed dp/sp value inline after sdp/ssp extension usages, e.g.
 * `12.Sdp` -> `12.Sdp = 14.4dp` (for the device width configured in
 * Settings | Tools | SDP/SSP Hints, default 360dp).
 *
 * Two formulas, matching the library:
 *  - `Sdp` / `Ssp` / `RSsp` (resource-bucket API): n x bucket(sw) / 300
 *  - `sdp` / `ssp` (multiplatform API):            n x sw / 360
 */
class SdpInlayHintsProvider : InlayHintsProvider {

    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? {
        if (file !is KtFile) return null
        return Collector
    }

    private object Collector : SharedBypassCollector {

        override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
            if (element !is KtDotQualifiedExpression) return
            val selector = element.selectorExpression as? KtNameReferenceExpression ?: return
            val name = selector.getReferencedName()
            if (name !in NAMES) return
            val value = numericLiteral(element.receiverExpression) ?: return

            val width = SdpSettings.getInstance().deviceWidthDp
            val hint = computeHint(name, value, width) ?: return

            sink.addPresentation(
                position = InlineInlayPosition(element.textRange.endOffset, relatedToPrevious = true),
                tooltip = "Computed for device width ${width}dp — change in Settings | Tools | SDP/SSP Hints",
                hintFormat = HintFormat.default,
            ) {
                text("= $hint")
            }
        }

        private val NAMES = setOf("Sdp", "Ssp", "RSsp", "sdp", "ssp")

        private fun computeHint(name: String, value: Double, width: Int): String? {
            val intValue = if (value == floor(value)) value.toInt() else null
            return when (name) {
                // Resource-bucket API (Android): only Int receivers, limited ranges.
                "Sdp" -> intValue?.let { n ->
                    val scaled = if (n in 1..600 || n in -60..-1) n * bucketScale(width) else n.toDouble()
                    format(scaled, "dp")
                }
                "Ssp" -> intValue?.let { n ->
                    val scaled = if (n in 1..600 || n in -60..-1) n * bucketScale(width) else n.toDouble()
                    format(scaled, "sp")
                }
                "RSsp" -> intValue?.let { n ->
                    val scaled = if (n in 1..100) n * bucketScale(width) else n.toDouble()
                    format(scaled, "sp")
                }
                // Multiplatform API: continuous scaling, any numeric receiver.
                "sdp" -> format(value * width / 360.0, "dp")
                "ssp" -> format(value * width / 360.0, "sp")
                else -> null
            }
        }

        /**
         * The resource dimens exist in 30dp buckets from sw300 to sw1080; below
         * sw300 the default bucket (scale 1.0) applies.
         */
        private fun bucketScale(width: Int): Double {
            if (width < 300) return 1.0
            val bucket = minOf(1080, width / 30 * 30)
            return bucket / 300.0
        }

        private fun format(value: Double, unit: String): String {
            val rounded = Math.round(value * 100) / 100.0
            val text = if (rounded == floor(rounded)) rounded.toInt().toString() else rounded.toString()
            return "$text$unit"
        }

        private fun numericLiteral(expression: KtExpression?): Double? = when (expression) {
            is KtConstantExpression ->
                expression.text.replace("_", "").trimEnd('f', 'F', 'L', 'l').toDoubleOrNull()
            is KtParenthesizedExpression -> numericLiteral(expression.expression)
            is KtPrefixExpression -> when (expression.operationToken) {
                KtTokens.MINUS -> numericLiteral(expression.baseExpression)?.let { -it }
                KtTokens.PLUS -> numericLiteral(expression.baseExpression)
                else -> null
            }
            else -> null
        }
    }
}
