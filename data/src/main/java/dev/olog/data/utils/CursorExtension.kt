package dev.olog.data.utils

import android.database.Cursor

fun Cursor.getLong(column: String): Long {
    return getLong(getColumnIndex(column))
}


fun Cursor.getString(column: String): String {
    return getString(getColumnIndex(column))
}