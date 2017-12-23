package dev.olog.presentation.utils

import android.content.Context
import dev.olog.presentation.R
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun formatMillis(context: Context, millis: Int): String {
        return formatMillis(context, millis.toLong())
    }

    fun formatMillis(context: Context, millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))

        return if (hours == 0L){
            context.resources.getQuantityString(R.plurals.plurals_minutes, minutes.toInt(), minutes.toInt())
        } else {
            var result = context.resources.getQuantityString(R.plurals.plurals_hours, hours.toInt(), hours.toInt()) + " "
            result += context.resources.getQuantityString(R.plurals.plurals_minutes, minutes.toInt(), minutes.toInt())
            result
        }
    }

}