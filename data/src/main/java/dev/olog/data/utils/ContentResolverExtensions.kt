package dev.olog.data.utils

import android.content.ContentResolver
import android.database.Cursor

// TODO extend on cursor, not on content resovler
@Suppress("unused")
internal inline fun <T> ContentResolver.queryAll(
    cursor: Cursor,
    mapper: (Cursor) -> T
): List<T> {
    val result = mutableListOf<T>()
    while (cursor.moveToNext()) {
        result.add(mapper(cursor))
    }
    cursor.close()

    return result
}

@Suppress("unused")
internal inline fun <T> ContentResolver.queryOne(
    cursor: Cursor,
    mapper: (Cursor) -> T
): T? {
    var item: T? = null
    if (cursor.moveToFirst()) {
        item = mapper(cursor)
    }
    cursor.close()

    return item
}

@Suppress("unused")
internal fun ContentResolver.queryCountRow(cursor: Cursor): Int {
    val count = cursor.count
    cursor.close()
    return count
}