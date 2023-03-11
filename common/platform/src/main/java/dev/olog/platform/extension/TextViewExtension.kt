@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.platform.extension

import android.widget.TextView

inline fun TextView.extractText(): String {
    return this.text.toString()
}