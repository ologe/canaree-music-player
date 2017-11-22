package dev.olog.shared

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

}
