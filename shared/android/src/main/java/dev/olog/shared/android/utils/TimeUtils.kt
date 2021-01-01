@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.utils

import android.content.Context
import dev.olog.shared.android.R
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.time.Duration

object TimeUtils {

    fun formatMillis(context: Context, duration: Duration): String {
        val millis = duration.toLongMilliseconds()
        val minutes = abs(millis / (1000 * 60) % 60) // 0..59
        val hours = abs(millis / (1000 * 60 * 60)) // 0..Long.MAX_VALUE

        if (hours == 0L) {
            return context.resources.getQuantityString(R.plurals.common_plurals_minutes, minutes.toInt(), minutes.toInt())
        }
        val h = context.resources.getQuantityString(R.plurals.common_plurals_hours, hours.toInt(), hours.toInt())
        val m = context.resources.getQuantityString(R.plurals.common_plurals_minutes, minutes.toInt(), minutes.toInt())
        return "$h $m"
    }

    fun timeAsMillis(hours: Int, minutes: Int, seconds: Int): Long {
        return TimeUnit.HOURS.toMillis(hours.toLong()) +
                TimeUnit.MINUTES.toMillis(minutes.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong())
    }

}