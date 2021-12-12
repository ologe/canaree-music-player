package dev.olog.core

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeFactory @Inject constructor() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun millisToFormattedDate(value: Long): String {
        return formatter.format(Date(value))
    }

    fun currentTimeMillis(): Long = System.currentTimeMillis()

}