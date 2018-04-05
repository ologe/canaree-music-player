package dev.olog.msc.utils


object TextUtils {

    const val MIDDLE_DOT = "\u00B7"
    const val MIDDLE_DOT_SPACED = " \u00B7 "

    fun formatMillis(millis: Int): String {
        return formatMillis(millis.toLong())
    }

    fun formatMillis(millis: Long, maintainZeros: Boolean = false): String {

        val second = millis / 1000 % 60
        val minute = millis / (1000 * 60) % 60
        val hour = millis / (1000 * 60 * 60) % 24

        if (hour == 0L && minute == 0L && second == 0L){
            if (maintainZeros){
                return "00:00"
            }
            return "0:00"
        }

        if (hour < 1){
            if (maintainZeros && minute < 10){
                return String.format("0%d:%d", minute, second)
            }
            return String.format("%d:%d", minute, second)
        }

        return String.format("%d:%d:%d", hour, minute, second)
    }

}