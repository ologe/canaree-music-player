package dev.olog.msc.utils.k.extension

import android.text.Spannable
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

fun TextView.extractText(): String {
    return this.text.toString()
}

@WorkerThread
fun TextView.precomputeText(text: Spannable): PrecomputedTextCompat {
    val textParams = TextViewCompat.getTextMetricsParams(this)
    return PrecomputedTextCompat.create(text, textParams)
}