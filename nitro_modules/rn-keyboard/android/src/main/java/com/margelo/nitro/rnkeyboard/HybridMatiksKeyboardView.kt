package com.margelo.nitro.rnkeyboard

import android.view.View
import com.facebook.react.uimanager.ThemedReactContext

class HybridMatiksKeyboardView(
  context: ThemedReactContext
) : HybridMatiksKeyboardViewSpec() {

  private val keyboardView = CustomKeyboardView(context)

  override val view: View
    get() = keyboardView

  override var customKeyboardType: KEYBOARD_TYPE? = null
    set(value) {
      field = value
      keyboardView.setKeyboardType(value?.name)
    }

  override var keyboardLayout: KEYBOARD_LAYOUT? = null
    set(value) {
      field = value
      keyboardView.setKeyboardLayout(value?.name)
    }

  override var hapticsEnabled: Boolean? = false
    set(value) {
      field = value
      keyboardView.setHapticsEnabled(value ?: false)
    }

  override var onKeyInput: ((event: KeyInputEvent) -> Unit)? = null
  override var onDelete: ((event: DeleteEvent) -> Unit)? = null

  init {
    keyboardView.setKeyListener(
      onKey = { key ->
        onKeyInput?.invoke(
          KeyInputEvent(
            key = key
          )
        )
      },
      onDelete = {
        onDelete?.invoke(
          DeleteEvent(
            deleted = true
          )
        )
      }
    )
  }
}
