package dev.olog.data.extension

import android.content.ContentValues

@Suppress("FunctionName")
fun ContentValues(vararg values: Pair<String, Any?>): ContentValues {
    val result = ContentValues(values.size)
    for ((key, value) in values) {
        when (value) {
            is Byte -> result.put(key, value)
            is Short -> result.put(key, value)
            is Int -> result.put(key, value)
            is Long -> result.put(key, value)
            is Float -> result.put(key, value)
            is Double -> result.put(key, value)
            is Boolean -> result.put(key, value)
            is ByteArray -> result.put(key, value)
            is String -> result.put(key, value)
            null -> result.putNull(key)
            else -> error("invalid $value")
        }
    }
    return result
}