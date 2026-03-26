package com.margelo.nitro.rnkeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class CustomKeyboardView(context: Context) : LinearLayout(context) {

  private var onKey: ((String) -> Unit)? = null
  private var onDelete: (() -> Unit)? = null

  fun setKeyListener(
    onKey: (String) -> Unit,
    onDelete: () -> Unit
  ) {
    this.onKey = onKey
    this.onDelete = onDelete
  }

  private var keyboardType: String = "NUMBERS"
  private var keyboardLayout: String = "TELEPHONE"
  private var isHapticsEnabled: Boolean = false

  private val TELEPHONE_KEYS = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(".", "0", "delete")
  )

  private val PHONE_ABILITY_KEYS = listOf(
    listOf("1", "2", "3", "-"),
    listOf("4", "5", "6", "/"),
    listOf("7", "8", "9", "clr"),
    listOf(".", "0", "space", "delete")
  )

  private val CALCULATOR_KEYS = listOf(
    listOf("7", "8", "9"),
    listOf("4", "5", "6"),
    listOf("1", "2", "3"),
    listOf(".", "0", "delete")
  )

  private val CALC_ABILITY_KEYS = listOf(
    listOf("7", "8", "9", "-"),
    listOf("4", "5", "6", "/"),
    listOf("1", "2", "3", "clr"),
    listOf(".", "0", "space", "delete")
  )

  private val COLOR_PRIMARY = Color.parseColor("#292929")
  private val COLOR_TERTIARY = Color.parseColor("#3A3A3A")
  private val COLOR_PLACEHOLDER = Color.parseColor("#777777")
  private val COLOR_WHITE = Color.WHITE
  private var keyboardHeightPx = dpToPx(248f).toInt()


  init {
    orientation = VERTICAL
    layoutParams = LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      keyboardHeightPx
    )
    // Add padding around the keyboard (left, top, right, bottom)
    val paddingPxHorizontal = dpToPx(2f).toInt()
    val paddingPxVertical = dpToPx(4f).toInt()
    setPadding(paddingPxHorizontal, paddingPxVertical, paddingPxHorizontal, paddingPxVertical)
    setBackgroundColor(COLOR_PRIMARY)
    setupKeyboard()
  }

  fun setKeyboardType(type: String?) {
    val newType = type ?: "NUMBERS"
    if (keyboardType != newType) {
      keyboardType = newType
      setupKeyboard()
    }
  }

  fun setKeyboardLayout(layout: String?) {
    val newLayout = layout ?: "TELEPHONE"
    if (keyboardLayout != newLayout) {
      keyboardLayout = newLayout
      setupKeyboard()
    }
  }

  fun setHapticsEnabled(enabled: Boolean) {
    isHapticsEnabled = enabled
  }

  private fun setupKeyboard() {
    removeAllViews()

    val use4Columns = keyboardType == "NUMBERS_AND_OPERATORS"

    val keys =
      if (keyboardLayout == "CALCULATOR") {
        if (use4Columns) CALC_ABILITY_KEYS else CALCULATOR_KEYS
      } else {
        if (use4Columns) PHONE_ABILITY_KEYS else TELEPHONE_KEYS
      }

    for (rowKeys in keys) {
      val rowLayout = LinearLayout(context).apply {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          dpToPx(52f).toInt()
        ).apply {
          // Vertical spacing between rows
          setMargins(0, dpToPx(3f).toInt(), 0, dpToPx(3f).toInt())
        }
      }

      for (key in rowKeys) {
        val keyContainer = createKeyView(key)
        rowLayout.addView(keyContainer)
      }

      addView(rowLayout)
    }
  }

  private fun createKeyView(key: String): FrameLayout {
    val container = FrameLayout(context).apply {
      layoutParams = LayoutParams(
        0,
        ViewGroup.LayoutParams.MATCH_PARENT,
        1f
      ).apply {
        // Horizontal spacing between buttons
        setMargins(dpToPx(4f).toInt(), 0, dpToPx(4f).toInt(), 0)
      }
    }

    val textView = TextView(context).apply {
      text = when (key) {
        "delete" -> "⌫"
        "space" -> "space"
        "clr" -> "C"
        else -> key
      }
      textSize = 18f
      typeface = Typeface.DEFAULT
      gravity = Gravity.CENTER
      setTextColor(COLOR_WHITE)
      layoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      background = GradientDrawable().apply {
        cornerRadius = dpToPx(5f)
        setColor(COLOR_TERTIARY)
      }
    }

    container.addView(textView)

    container.setOnTouchListener { v, event ->
      val bg = textView.background as GradientDrawable
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          bg.setColor(COLOR_PLACEHOLDER)
          if (isHapticsEnabled) {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
          }
          handleKeyPress(key)
        }
        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_CANCEL -> {
          bg.setColor(COLOR_TERTIARY)
          v.performClick()
        }
      }
      true
    }

    return container
  }

  private fun handleKeyPress(key: String) {
    if (key == "delete") {
      onDelete?.invoke()
    } else {
      onKey?.invoke(key)
    }
  }

  private fun dpToPx(dp: Float): Float =
    TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      dp,
      resources.displayMetrics
    )
}
