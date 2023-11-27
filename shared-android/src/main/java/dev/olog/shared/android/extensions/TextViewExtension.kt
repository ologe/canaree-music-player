@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.widget.TextView

inline fun TextView.extractText(): String {
    return this.text.toString()
}