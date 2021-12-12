package dev.olog.shared

import java.util.*

fun String.titlecase(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}