package com.margelo.nitro.rnstroketext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import kotlin.math.ceil
import kotlin.math.max

class StrokeTextView(context: Context) : View(context) {

    // ─────────────────────────────────────────────
    // Props
    // ─────────────────────────────────────────────

    private var text: String = ""
    private var fontSizePx: Float = sp(14f)
    private var textColor: Int = Color.BLACK
    private var strokeColor: Int = Color.WHITE
    private var strokeWidthPx: Float = dp(1f)
    private var fontFamily: String = "sans-serif"
    private var numberOfLines: Int = 0
    private var ellipsis: Boolean = false
    private var alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
    private var customWidthPx: Float = 0f

    // ─────────────────────────────────────────────
    // Paints
    // ─────────────────────────────────────────────

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val strokePaint =
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }

    // ─────────────────────────────────────────────
    // Layout
    // ─────────────────────────────────────────────

    private var textLayout: StaticLayout? = null
    private var strokeLayout: StaticLayout? = null
    private var layoutDirty = true
    private var lastLayoutWidth: Int = -1 // Track width used for layout

    private val fontCache = HashMap<String, Typeface?>()

    // ─────────────────────────────────────────────
    // Measurement (RN / Nitro safe)
    // ─────────────────────────────────────────────

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        updatePaints()

        val measuredWidth = resolveMeasuredWidth(widthMeasureSpec)
        ensureLayout(measuredWidth)

        val desiredHeight = (textLayout?.height ?: 0).coerceAtLeast(1)

        setMeasuredDimension(measuredWidth, resolveSize(desiredHeight, heightMeasureSpec))
    }

    private fun resolveMeasuredWidth(spec: Int): Int {
        val mode = MeasureSpec.getMode(spec)
        val size = MeasureSpec.getSize(spec)

        // Width explicitly provided from JS prop
        if (customWidthPx > 0f) {
            return ceil(customWidthPx).toInt().coerceAtLeast(1)
        }

        // Exact width from Yoga
        if (mode == MeasureSpec.EXACTLY && size > 0) {
            return size
        }

        // Intrinsic width from text
        val textWidth = ceil(measureTextWidth()).toInt().coerceAtLeast(1)

        return when (mode) {
            MeasureSpec.AT_MOST -> minOf(size, textWidth)
            else -> textWidth
        }
    }

    // ─────────────────────────────────────────────
    // Layout creation
    // ─────────────────────────────────────────────

    private fun updatePaints() {
        val typeface = getFont(fontFamily)

        textPaint.apply {
            this.typeface = typeface
            textSize = fontSizePx
            color = textColor
        }

        strokePaint.apply {
            this.typeface = typeface
            textSize = fontSizePx
            strokeWidth = strokeWidthPx
            color = strokeColor
        }
    }

    private fun ensureLayout(width: Int) {
        val safeWidth = width.coerceAtLeast(1)
        if (!layoutDirty && textLayout != null && lastLayoutWidth == safeWidth) return
        updatePaints()

        var displayText: CharSequence =
                if (ellipsis) {
                    TextUtils.ellipsize(
                            text,
                            textPaint,
                            safeWidth.toFloat(),
                            TextUtils.TruncateAt.END
                    )
                } else {
                    text
                }

        // Use StaticLayout.Builder for API 23+
        val builder =
                StaticLayout.Builder.obtain(
                                displayText,
                                0,
                                displayText.length,
                                textPaint,
                                safeWidth
                        )
                        .setAlignment(alignment)
                        .setLineSpacing(0f, 1f)
                        .setIncludePad(false)

        var layout = builder.build()

        if (numberOfLines > 0 && layout.lineCount > numberOfLines) {
            val end = layout.getLineEnd(numberOfLines - 1)
            displayText = displayText.subSequence(0, end)

            val truncatedBuilder =
                    StaticLayout.Builder.obtain(
                                    displayText,
                                    0,
                                    displayText.length,
                                    textPaint,
                                    safeWidth
                            )
                            .setAlignment(alignment)
                            .setLineSpacing(0f, 1f)
                            .setIncludePad(false)

            layout = truncatedBuilder.build()
        }

        textLayout = layout

        // For stroke layout, we use the same text
        val strokeBuilder =
                StaticLayout.Builder.obtain(
                                displayText,
                                0,
                                displayText.length,
                                strokePaint,
                                safeWidth
                        )
                        .setAlignment(alignment)
                        .setLineSpacing(0f, 1f)
                        .setIncludePad(false)

        strokeLayout = strokeBuilder.build()

        lastLayoutWidth = safeWidth // Remember the width used
        layoutDirty = false
    }

    fun getTextDimensions(): Pair<Double, Double> {
        updatePaints()

        // Ensure we have a valid layout for measurement.
        // If width is 0/unspecified, we measure intrinsic width.
        val measureWidth =
                if (customWidthPx > 0f) {
                    ceil(customWidthPx).toInt()
                } else {
                    ceil(measureTextWidth()).toInt()
                }

        // We create a temporary layout if needed to get accurate height for the given width
        // But since ensureLayout caches the layout based on last width, we might want to just
        // ensure layout exists.
        // For accurate "intrinsic" measurement, we usually want the width to be the text width if
        // not constrained.

        ensureLayout(measureWidth)

        val wPx = textLayout?.width?.toFloat() ?: 0f
        val hPx = textLayout?.height?.toFloat() ?: 0f

        return Pair(pxToDp(wPx).toDouble(), pxToDp(hPx).toDouble())
    }

    private fun measureTextWidth(): Float {
        var maxWidth = 0f
        for (line in text.split("\n")) {
            maxWidth = max(maxWidth, textPaint.measureText(line))
        }
        return maxWidth + strokeWidthPx * 2 + 4
    }

    // ─────────────────────────────────────────────
    // Drawing
    // ─────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        ensureLayout(width)
        strokeLayout?.draw(canvas)
        textLayout?.draw(canvas)
    }

    // ─────────────────────────────────────────────
    // Props setters (Nitro)
    // ─────────────────────────────────────────────

    fun setText(value: String) {
        if (text != value) {
            text = value
            invalidateLayout()
        }
    }

    fun setFontSize(value: Float) {
        val px = dp(value)
        if (fontSizePx != px) {
            fontSizePx = px
            invalidateLayout()
        }
    }

    fun setTextColor(value: String) {
        val c = parseColor(value)
        if (textColor != c) {
            textColor = c
            invalidate()
        }
    }

    fun setStrokeColor(value: String) {
        val c = parseColor(value)
        if (strokeColor != c) {
            strokeColor = c
            invalidate()
        }
    }

    fun setStrokeWidth(value: Float) {
        val px = dp(value)
        if (strokeWidthPx != px) {
            strokeWidthPx = px
            invalidateLayout()
        }
    }

    fun setFontFamily(value: String) {
        if (fontFamily != value) {
            fontFamily = value
            invalidateLayout()
        }
    }

    fun setTextAlignment(value: String) {
        val newAlignment =
                when (value) {
                    "left" -> Layout.Alignment.ALIGN_NORMAL
                    "right" -> Layout.Alignment.ALIGN_OPPOSITE
                    "center" -> Layout.Alignment.ALIGN_CENTER
                    else -> alignment
                }
        if (alignment != newAlignment) {
            alignment = newAlignment
            invalidateLayout()
        }
    }

    fun setNumberOfLines(value: Int) {
        if (numberOfLines != value) {
            numberOfLines = value
            invalidateLayout()
        }
    }

    fun setEllipsis(value: Boolean) {
        if (ellipsis != value) {
            ellipsis = value
            invalidateLayout()
        }
    }

    fun setCustomWidth(value: Float) {
        val px = dp(value)
        if (customWidthPx != px) {
            customWidthPx = px
            invalidateLayout()
        }
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private fun invalidateLayout() {
        layoutDirty = true
        requestLayout()
        invalidate()
    }

    private fun sp(v: Float): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, resources.displayMetrics)

    private fun dp(v: Float): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics)

    private fun pxToDp(px: Float): Float = px / resources.displayMetrics.density

    private fun parseColor(color: String): Int =
            try {
                Color.parseColor(color)
            } catch (_: Exception) {
                Color.BLACK
            }

    private fun getFont(fontFamily: String): Typeface? {
        return fontCache[fontFamily]
                ?: run {
                    val tf = FontUtil.getFont(context, fontFamily)
                    fontCache[fontFamily] = tf
                    tf
                }
    }

    init {
        setWillNotDraw(false)
    }
}
