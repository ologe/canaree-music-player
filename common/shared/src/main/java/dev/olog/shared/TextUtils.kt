package dev.olog.shared

import kotlin.math.abs


object TextUtils {

    const val MIDDLE_DOT = "\u00B7"
    const val MIDDLE_DOT_SPACED = " \u00B7 "

    fun getTrackText(
        title: String,
        subtitle: String,
    ) : String {
        return "$title${MIDDLE_DOT_SPACED}$subtitle"
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    inline fun formatMillis(millis: Int): String {
        return formatMillis(millis.toLong())
    }

    @JvmStatic
    fun formatMillis(millis: Long, maintainZeros: Boolean = false): String {

        val isNegative = millis < 0L
        val second = abs(millis / 1000 % 60)
        val minute = abs(millis / (1000 * 60) % 60)
        val hour = abs(millis / (1000 * 60 * 60) % 24)

        if (hour == 0L && minute == 0L && second == 0L){
            if (maintainZeros){
                return "00:00"
            }
            return "0:00"
        }

        val formattedSeconds = if (second < 10) "0%d" else "%d"
        val formattedMinutes = if (minute < 10) "0%d" else "%d"

        val result: String

        if (hour < 1){
            if (maintainZeros && minute < 10){
                result = String.format("0%d:$formattedSeconds", minute, second)
            } else {
                result = String.format("%d:$formattedSeconds", minute, second)
            }
        } else {
            result = String.format("%d:$formattedMinutes:$formattedMinutes", hour, minute, second)
        }


        if (isNegative){
            return "-$result"
        }
        return result
    }

    @JvmStatic
    fun addSpacesToDash(original: String): String{
        return original
                .trim()
                .replace("-", " - ")
                .replace("\\s+".toRegex(), " ")
    }

}