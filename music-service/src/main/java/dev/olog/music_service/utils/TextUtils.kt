package dev.olog.music_service.utils

import dev.olog.shared.TimeUtils

object TextUtils {

    fun formatTimeMillis(millis: Long): String {
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