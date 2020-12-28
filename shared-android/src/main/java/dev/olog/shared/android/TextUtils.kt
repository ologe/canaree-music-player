package dev.olog.shared.android

import kotlin.math.abs
import kotlin.time.Duration

object TextUtils {

    const val MIDDLE_DOT = "\u00B7"
    const val MIDDLE_DOT_SPACED = " \u00B7 "

    @Suppress("NOTHING_TO_INLINE")
    inline fun formatTimeMillis(millis: Duration, maintainZeros: Boolean = false): String {
        return formatTimeMillis(millis.toLongMilliseconds(), maintainZeros)
    }

    fun formatTimeMillis(millis: Long, maintainZeros: Boolean = false): String {
        val second = abs(millis / 1000 % 60) // 0..59
        val minute = abs(millis / (1000 * 60) % 60) // 0..59
        val hour = abs(millis / (1000 * 60 * 60)) // 0..Long.MAX_VALUE

        if (millis <= 0L) {
            val pads = if (maintainZeros) 2 else 1
            return "${"0".padStart(pads, '0')}:00"
        }

        if (hour == 0L) {
            val pads = if (maintainZeros) 2 else 1
            return buildString {
                append(minute.toString().padStart(pads, '0'))
                append(":")
                append(second.toString().padStart(2, '0'))
            }
        }

        return buildString {
            append(hour)
            append(":")
            append(minute.toString().padStart(2, '0'))
            append(":")
            append(second.toString().padStart(2, '0'))
        }
    }

}