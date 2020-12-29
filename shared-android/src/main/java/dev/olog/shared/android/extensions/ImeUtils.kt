package dev.olog.shared.android.extensions

import android.view.inputmethod.InputMethodManager
import android.widget.TextView

// TODO improve
fun TextView.showIme() {
    isFocusable = true
    if (requestFocus()) {
        val inputManager = context.systemService<InputMethodManager>()
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

// TODO improve
fun TextView.hideIme() {
    val inputManager = context.systemService<InputMethodManager>()
    clearFocus()
    inputManager.hideSoftInputFromWindow(windowToken, 0)

}