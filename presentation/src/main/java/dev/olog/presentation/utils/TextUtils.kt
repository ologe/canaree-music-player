package dev.olog.presentation.utils

import java.util.concurrent.TimeUnit

object TextUtils {

    @JvmStatic
    fun getReadableSongLength(millis: Long): String {
        val sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        val format = if (sec < 10) "%d:0%d" else "%d:%d"
        return String.format(format, TimeUnit.MILLISECONDS.toMinutes(millis), sec)
    }

}