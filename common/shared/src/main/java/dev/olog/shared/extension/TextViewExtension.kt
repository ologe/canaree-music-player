package dev.olog.shared.extension

import android.widget.TextView

@Suppress("NOTHING_TO_INLINE")
inline fun TextView.extractText(): String {
    return this.text.toString()
}