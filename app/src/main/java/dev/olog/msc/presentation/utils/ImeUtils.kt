package dev.olog.msc.presentation.utils

import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.content.systemService

object ImeUtils {

    fun showIme(editText: TextView) {
        editText.isFocusable = true
        if (editText.requestFocus()){
            val context = editText.context
            val inputManager = context.systemService<InputMethodManager>()
            inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideIme(editText: TextView) {
        val context = editText.context
        val inputManager = context.systemService<InputMethodManager>()
        editText.clearFocus()
        inputManager.hideSoftInputFromWindow(editText.windowToken, 0)

    }


}