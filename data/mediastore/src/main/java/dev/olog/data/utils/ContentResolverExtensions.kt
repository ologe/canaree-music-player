package dev.olog.data.utils

import android.content.ContentResolver
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

// TODO inject schedulers??
// TODO cache indexes?
@Suppress("unused")
internal suspend inline fun <T> ContentResolver.queryAll(
    cursor: Cursor,
    crossinline mapper: suspend (Cursor) -> T?
): List<T> = withContext(Dispatchers.IO) {

    cursor.use {
        buildList {
            while (it.moveToNext()) {
                yield()
                add(mapper(it) ?: continue)
            }
        }
    }
}

@Suppress("unused")
internal suspend inline fun <T> ContentResolver.queryOne(
    cursor: Cursor,
    crossinline mapper: suspend (Cursor) -> T?
): T? = withContext(Dispatchers.IO) {

    cursor.use {
        if (it.moveToFirst()) {
            yield()
            mapper(it)
        } else {
            yield()
            null
        }
    }
}

@Suppress("unused")
internal suspend fun ContentResolver.queryCountRow(
    cursor: Cursor
): Int = withContext(Dispatchers.IO) {

    cursor.use {
        yield()
        cursor.count
    }
}