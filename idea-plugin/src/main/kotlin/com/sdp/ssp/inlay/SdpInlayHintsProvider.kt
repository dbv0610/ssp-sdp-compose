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
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import kotlin.math.floor

/**
 * Shows the computed dp/sp value inline after sdp/ssp extension usages, e.g.
 * `12.Sdp` -> `12.Sdp 420 ×1.4 → 16.8dp` (width × scale → value, for the
 * device width configured in Settings | Tools | SDP/SSP Hints, default 420dp).
 *
 * Besides literals, the receiver value is also inferred (PSI-only, no
 * resolve) from local `val`s with literal initializers, lambda parameters
 * fed by literal collections (`listOf(40, 80).forEach { it.Sdp }`, shown as
 * `→ 56/112dp`), and `for`-loop variables over literal collections.
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
            val values = resolveValues(element.receiverExpression) ?: return

            val width = SdpSettings.getInstance().deviceWidthDp
            val scaled = values.mapNotNull { scaledValue(name, it, width) }
            if (scaled.isEmpty()) return

            val unit = if (name == "Sdp" || name == "sdp") "dp" else "sp"
            val scale = when (name) {
                "sdp", "ssp" -> width / 360.0
                else -> bucketScale(width)
            }
            val shown = scaled.take(MAX_SHOWN).joinToString("/") { formatNumber(it) }
            val suffix = if (scaled.size > MAX_SHOWN) "…" else ""

            sink.addPresentation(
                position = InlineInlayPosition(element.textRange.endOffset, relatedToPrevious = true),
                tooltip = "Computed for device width ${width}dp — change in Settings | Tools | SDP/SSP Hints",
                hintFormat = HintFormat.default,
            ) {
                text("$width ×${formatNumber(scale)} → $shown$unit$suffix")
            }
        }

        private val NAMES = setOf("Sdp", "Ssp", "RSsp", "sdp", "ssp")
        private val SCOPE_CALLS = setOf("forEach", "onEach", "map", "let", "also")
        private val COLLECTION_FACTORIES = setOf(
            "listOf", "mutableListOf", "setOf", "arrayOf",
            "intArrayOf", "floatArrayOf", "doubleArrayOf", "arrayListOf",
        )
        private const val MAX_SHOWN = 4

        // ── receiver value inference ─────────────────────────────────────────

        /**
         * Values the receiver may take, or null when they cannot be inferred.
         * Walks outward from the reference so the innermost binding of the
         * name wins (correct shadowing without semantic resolve).
         */
        private fun resolveValues(expression: KtExpression?): List<Double>? {
            numericLiteral(expression)?.let { return listOf(it) }
            val reference = expression as? KtNameReferenceExpression ?: return null
            val name = reference.getReferencedName()

            var node: PsiElement = reference
            while (true) {
                val parent = node.parent ?: return null
                if (parent is KtBlockExpression) {
                    var sibling = node.prevSibling
                    while (sibling != null) {
                        if (sibling is KtProperty && sibling.name == name) {
                            if (sibling.isVar) return null
                            return numericLiteral(sibling.initializer)?.let { listOf(it) }
                        }
                        sibling = sibling.prevSibling
                    }
                }
                if (parent is KtFunctionLiteral && bindsName(parent, name)) {
                    return lambdaInputValues(parent)
                }
                if (parent is KtForExpression && parent.loopParameter?.name == name) {
                    return literalCollectionValues(parent.loopRange, reference)
                }
                if (parent is KtFile) return null
                node = parent
            }
        }

        private fun bindsName(lambda: KtFunctionLiteral, name: String): Boolean =
            (name == "it" && lambda.valueParameters.isEmpty()) ||
                lambda.valueParameters.any { it.name == name }

        /** For `receiver.forEach { x.Sdp }`-style calls, the receiver's literal values. */
        private fun lambdaInputValues(lambda: KtFunctionLiteral): List<Double>? {
            val lambdaExpression = lambda.parent as? KtLambdaExpression ?: return null
            val call = when (val argument = lambdaExpression.parent) {
                is KtLambdaArgument -> argument.parent as? KtCallExpression
                is KtCallExpression -> argument // lambda passed inside parentheses
                else -> null
            } ?: return null
            val callee = (call.calleeExpression as? KtNameReferenceExpression)
                ?.getReferencedName() ?: return null
            if (callee !in SCOPE_CALLS) return null
            val qualified = call.parent as? KtDotQualifiedExpression ?: return null
            if (qualified.selectorExpression !== call) return null
            return literalCollectionValues(qualified.receiverExpression, qualified)
        }

        /**
         * Literal values of a collection expression: `listOf(40, 80)`, a plain
         * literal (`40.let { ... }`), or a local `val` holding either.
         */
        private fun literalCollectionValues(expression: KtExpression?, context: PsiElement): List<Double>? {
            when (expression) {
                is KtCallExpression -> {
                    val callee = (expression.calleeExpression as? KtNameReferenceExpression)
                        ?.getReferencedName() ?: return null
                    if (callee !in COLLECTION_FACTORIES) return null
                    val values = expression.valueArguments.map {
                        numericLiteral(it.getArgumentExpression()) ?: return null
                    }
                    return values.ifEmpty { null }
                }
                is KtNameReferenceExpression -> {
                    val initializer = localValInitializer(expression) ?: return null
                    return literalCollectionValues(initializer, expression)
                }
                else -> return numericLiteral(expression)?.let { listOf(it) }
            }
        }

        /** Initializer of the nearest local `val <name>` declared before the reference. */
        private fun localValInitializer(reference: KtNameReferenceExpression): KtExpression? {
            val name = reference.getReferencedName()
            var node: PsiElement = reference
            while (true) {
                val parent = node.parent ?: return null
                if (parent is KtBlockExpression) {
                    var sibling = node.prevSibling
                    while (sibling != null) {
                        if (sibling is KtProperty && sibling.name == name) {
                            return if (sibling.isVar) null else sibling.initializer
                        }
                        sibling = sibling.prevSibling
                    }
                }
                if (parent is KtFile) return null
                node = parent
            }
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

        // ── value computation ────────────────────────────────────────────────

        private fun scaledValue(name: String, value: Double, width: Int): Double? {
            val intValue = if (value == floor(value)) value.toInt() else null
            return when (name) {
                // Resource-bucket API (Android): only Int receivers, limited ranges.
                "Sdp", "Ssp" -> intValue?.let { n ->
                    if (n in 1..600 || n in -60..-1) n * bucketScale(width) else n.toDouble()
                }
                "RSsp" -> intValue?.let { n ->
                    if (n in 1..100) n * bucketScale(width) else n.toDouble()
                }
                // Multiplatform API: continuous scaling, any numeric receiver.
                "sdp", "ssp" -> value * width / 360.0
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

        private fun formatNumber(value: Double): String {
            val rounded = Math.round(value * 100) / 100.0
            return if (rounded == floor(rounded)) rounded.toInt().toString() else rounded.toString()
        }
    }
}
