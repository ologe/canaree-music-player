@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.extensions

import android.text.Spannable
import android.widget.TextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import dev.olog.shared.utils.assertBackgroundThread

inline fun TextView.extractText(): String {
    return this.text.toString()
}

fun TextView.precomputeText(text: Spannable): PrecomputedTextCompat {
    assertBackgroundThread()
    val textParams = TextViewCompat.getTextMetricsParams(this)
    return PrecomputedTextCompat.create(text, textParams)
}