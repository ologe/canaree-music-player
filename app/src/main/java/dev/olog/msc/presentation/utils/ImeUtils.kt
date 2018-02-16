package dev.olog.msc.presentation.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

object ImeUtils {

    fun showIme(editText: TextView) {
        editText.isFocusable = true
        if (editText.requestFocus()){
            val context = editText.context
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideIme(editText: TextView) {
        val context = editText.context
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.clearFocus()
        inputManager.hideSoftInputFromWindow(editText.windowToken, 0)

    }


}