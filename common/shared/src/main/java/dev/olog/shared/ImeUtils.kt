package dev.olog.shared

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun TextView.showIme() {
    isFocusable = true
    if (requestFocus()) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun TextView.hideIme() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    clearFocus()
    inputManager.hideSoftInputFromWindow(windowToken, 0)

}