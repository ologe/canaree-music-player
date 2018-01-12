package dev.olog.shared_android

import java.util.concurrent.TimeUnit

object TextUtils {

    const val MIDDLE_DOT = "\u00B7"
    const val MIDDLE_DOT_SPACED = " \u00B7 "

    fun getReadableSongLength(millis: Long): String {
        val sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        val format = if (sec < 10) "%d:0%d" else "%d:%d"
        return String.format(format, TimeUnit.MILLISECONDS.toMinutes(millis), sec)
    }


    fun formatTimeMillisForNotification(millis: Long): String {
        val seconds = TimeUtils.extractSeconds(millis)
        val minutes = TimeUtils.extractMinutes(millis)
        val builder = StringBuilder()
                .append(if (minutes < 10) "0" else "")
                .append("%d:")
                .append(if (seconds < 10) "0" else "")
                .append("%d")
        return String.format(builder.toString(), minutes, seconds)
    }

}