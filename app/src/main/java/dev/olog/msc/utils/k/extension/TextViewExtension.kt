package dev.olog.msc.utils.k.extension

import android.widget.TextView

fun TextView.extractText(): String {
    return this.text.toString()
}