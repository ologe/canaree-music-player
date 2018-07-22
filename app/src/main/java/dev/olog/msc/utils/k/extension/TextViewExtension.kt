package dev.olog.msc.utils.k.extension

import android.support.annotation.WorkerThread
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.text.Spannable
import android.widget.TextView

fun TextView.extractText(): String {
    return this.text.toString()
}

@WorkerThread
fun TextView.precomputeText(text: Spannable): PrecomputedTextCompat {
    val textParams = TextViewCompat.getTextMetricsParams(this)
    return PrecomputedTextCompat.create(text, textParams)
}