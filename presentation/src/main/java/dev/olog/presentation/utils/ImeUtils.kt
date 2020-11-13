package dev.olog.presentation.utils

import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import dev.olog.shared.android.extensions.systemService

fun TextView.showIme() {
    isFocusable = true
    if (requestFocus()) {
        val inputManager = context.systemService<InputMethodManager>()
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun TextView.hideIme() {
    val inputManager = context.systemService<InputMethodManager>()
    clearFocus()
    inputManager.hideSoftInputFromWindow(windowToken, 0)

}