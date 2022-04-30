package dev.olog.shared.extension

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

inline fun <reified T : Any> SharedPreferences.observeKey(key: String, default: T): Flow<T> {
    val flow = MutableStateFlow<T?>(null)
    flow.value = getItem(key, default)

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (key == k) {
            flow.value = getItem(key, default)
        }
    }

    return flow
        .onStart { registerOnSharedPreferenceChangeListener(listener) }
        .onCompletion { unregisterOnSharedPreferenceChangeListener(listener) }
        .filterNotNull()
}

inline fun <reified T> SharedPreferences.getItem(key: String, default: T): T {
    @Suppress("UNCHECKED_CAST")
    return when (default){
        is String -> getString(key, default) as T
        is Int -> getInt(key, default) as T
        is Long -> getLong(key, default) as T
        is Boolean -> getBoolean(key, default) as T
        is Float -> getFloat(key, default) as T
        is Set<*> -> getStringSet(key, default as Set<String>) as T
        is MutableSet<*> -> getStringSet(key, default as MutableSet<String>) as T
        else -> throw IllegalArgumentException("generic type not handle ${T::class.java.name}")
    }
}