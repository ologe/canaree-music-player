package dev.olog.shared

import android.content.SharedPreferences
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.extensions.safeSend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

inline fun <reified T> SharedPreferences.observeKey(key: String, default: T): Flow<T> {
    val flow: Flow<T> = channelFlow {
        safeSend(getItem(key, default))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                launch {
                    safeSend(getItem(key, default)!!)
                }
            }
        }

        registerOnSharedPreferenceChangeListener(listener)
        invokeOnClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return flow.assertBackground()
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> SharedPreferences.getItem(key: String, default: T): T {
    return when {
        String::class.java.isAssignableFrom(T::class.java) -> getString(key, default as String) as T
        Int::class.java.isAssignableFrom(T::class.java) -> getInt(key, default as Int) as T
        Long::class.java.isAssignableFrom(T::class.java) -> getLong(key, default as Long) as T
        Boolean::class.java.isAssignableFrom(T::class.java) -> getBoolean(key, default as Boolean) as T
        Float::class.java.isAssignableFrom(T::class.java) -> getFloat(key, default as Float) as T
        Set::class.java.isAssignableFrom(T::class.java) -> getStringSet(key, default as Set<String>) as T
        MutableSet::class.java.isAssignableFrom(T::class.java) -> getStringSet(key, default as MutableSet<String>) as T
        else -> throw IllegalArgumentException("generic type not handle ${T::class.java.name}")
    }
}