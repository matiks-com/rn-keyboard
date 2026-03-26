package com.margelo.nitro.matiksbutton

import android.content.Context
import android.view.View
import android.widget.Button
import com.margelo.nitro.matiksbutton.HybridButtonViewSpec

class HybridButtonView(
  context: Context
) : HybridButtonViewSpec() {

  private val button = Button(context)

  // Props
  override var title: String = ""
    set(value) {
      field = value
      button.text = value
    }

  override var onPress: () -> Unit = {}

  override val view: View
    get() = button

  init {
    button.setOnClickListener {
      onPress.invoke()
    }
  }
}
