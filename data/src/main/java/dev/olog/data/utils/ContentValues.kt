package dev.olog.data.utils

import android.content.ContentValues

internal fun ContentValues(vararg values: Pair<String, Any?>): ContentValues {
    return ContentValues().apply {
        for ((key, value) in values) {
            when (value) {
                null -> putNull(key)
                is Boolean -> put(key, value)
                is Byte -> put(key, value)
                is Short -> put(key, value)
                is Int -> put(key, value)
                is Long -> put(key, value)
                is Float -> put(key, value)
                is Double -> put(key, value)
                is String -> put(key, value)
                is ByteArray -> put(key, value)
                else -> error("invalid $key - $value")
            }
        }
    }
}