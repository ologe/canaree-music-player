package dev.olog.data.local

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

internal class DateTimeGenerator @Inject constructor() {

    companion object {
        private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun now(): Long {
        return System.currentTimeMillis()
    }

    fun formattedNow(): String {
        return formatter.format(Date(now()))
    }

}