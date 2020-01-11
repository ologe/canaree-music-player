@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.widget.EditText

inline fun EditText.extractText(): String {
    return this.text.toString()
}