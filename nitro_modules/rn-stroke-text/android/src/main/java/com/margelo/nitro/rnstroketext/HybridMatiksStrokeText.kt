package com.margelo.nitro.rnstroketext
import com.margelo.nitro.rnstroketext.HybridMatiksStrokeTextSpec
import android.content.Context
import android.view.View
import com.margelo.nitro.rnstroketext.TextAlign

class HybridMatiksStrokeText(
  context: Context
) : HybridMatiksStrokeTextSpec() {
    private val strokeTextView = StrokeTextView(context)

    override val view: View
        get() = strokeTextView

    override fun measureDimensions(): Dimensions {
        val (width, height) = strokeTextView.getTextDimensions()
        return Dimensions(width, height)
    }

    // props
    override var width: Double? = 0.0
        set(value) {
            field = value
            strokeTextView.setCustomWidth(value?.toFloat() ?: 0f)
        }

    override var text: String = ""
        set(value) {
            field = value
            strokeTextView.setText(value)
        }

    override var fontSize: Double? = 0.0
        set(value) {
            field = value
            strokeTextView.setFontSize(value?.toFloat() ?: 0f)
        }

    override var color: String? = ""
        set(value) {
            field = value
            strokeTextView.setTextColor(value ?: "")
        }

    override var strokeColor: String? = ""
        set(value) {
            field = value
            strokeTextView.setStrokeColor(value ?: "")
        }

    override var strokeWidth: Double? = 0.0
        set(value) {
            field = value
            strokeTextView.setStrokeWidth(value?.toFloat() ?: 0f)
        }

    override var fontFamily: String? = ""
        set(value) {
            field = value
            strokeTextView.setFontFamily(value ?: "")
        }

    override var align: TextAlign? = TextAlign.CENTER
        set(value) {
            field = value
            strokeTextView.setTextAlignment(value?.name?.lowercase() ?: "center")
        }

    override var numberOfLines: Double? = 0.0
        set(value) {
            field = value
            strokeTextView.setNumberOfLines(value?.toInt() ?: 0)
        }

    override var ellipsis: Boolean? = false
        set(value) {
            field = value
            strokeTextView.setEllipsis(value ?: false)
        }
}