package dev.olog.presentation.model

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

inline fun <reified T> SharedPreferences.observeKey(key: String, default: T, dispatcher: CoroutineContext = Dispatchers.Default): Flow<T> {
    val flow: Flow<T> = channelFlow {
        trySend(getItem(key, default))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                trySend(getItem(key, default)!!)
            }
        }

        registerOnSharedPreferenceChangeListener(listener)
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return flow
        .flowOn(dispatcher)
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