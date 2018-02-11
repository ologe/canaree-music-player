package dev.olog.msc.utils

import android.content.Context
import dev.olog.msc.R
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun extractHours(millis: Long): Long {
        return millis / (1000 * 60 * 60)
    }

    fun extractMinutes(millis: Long): Long {
        return millis / (1000 * 60) % 60
    }

    fun extractSeconds(millis: Long): Long {
        return millis / 1000 % 60
    }

    fun formatMillis(context: Context, millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))

        if (hours == 0L) {
            return context.resources.getQuantityString(R.plurals.plurals_minutes, minutes.toInt(), minutes.toInt())
        } else {
            var result = context.resources.getQuantityString(R.plurals.plurals_hours, hours.toInt(), hours.toInt()) + " "
            result += context.resources.getQuantityString(R.plurals.plurals_minutes, minutes.toInt(), minutes.toInt())
            return result
        }
    }

}