package dev.olog.msc.utils

import dev.olog.msc.constants.AppConstants
import java.text.Collator

fun Collator.safeCompare(source: String, target: String): Int {
    val s = if (source == AppConstants.UNKNOWN) source.substring(1)
                else source
    val t = if (target == AppConstants.UNKNOWN) target.substring(1)
                else target
    return this.compare(s, t)
}