@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.mediastore.utils

import android.database.Cursor

internal inline fun Cursor.getInt(columnName: String): Int {
    try {
        return this.getInt(this.getColumnIndex(columnName))
    } catch (ex: IllegalStateException) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getLong(columnName: String): Long {
    try {
        return this.getLong(this.getColumnIndex(columnName))
    } catch (ex: IllegalStateException) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getString(columnName: String): String {
    try {
        return this.getString(this.getColumnIndex(columnName))
    } catch (ex: IllegalStateException) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getIntOrNull(columnName: String): Int? {
    try {
        return this.getInt(this.getColumnIndex(columnName))
    } catch (ex: IllegalStateException) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal inline fun Cursor.getLongOrNull(columnName: String): Long? {
    try {
        return this.getLong(this.getColumnIndex(columnName))
    } catch (ex: IllegalStateException) {
        throw IllegalStateException("invalid column $columnName", ex)
    }

}

internal inline fun Cursor.getStringOrNull(columnName: String): String? {
    try {
        return this.getString(this.getColumnIndex(columnName))
    } catch (ex: IllegalStateException) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}