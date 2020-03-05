@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.utils

import android.database.Cursor
import timber.log.Timber

internal inline fun Cursor.getInt(columnName: String): Int {
    try {
        return this.getInt(this.getColumnIndex(columnName))
    } catch (ex: Exception) {
        Timber.e(ex, "column $columnName")
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getLong(columnName: String): Long {
    try {
        return this.getLong(this.getColumnIndex(columnName))
    } catch (ex: Exception) {
        Timber.e(ex, "column $columnName")
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getString(columnName: String): String {
    try {
        return this.getString(this.getColumnIndex(columnName))
    } catch (ex: Exception) {
        Timber.e(ex, "column $columnName")
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getStringOrNull(columnName: String): String? {
    try {
        return this.getString(this.getColumnIndex(columnName))
    } catch (ex: Exception) {
        Timber.e(ex, "column $columnName")
        throw IllegalStateException("invalid column $columnName", ex)
    }
}