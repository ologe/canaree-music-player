package dev.olog.core

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) {

    fun <T1 : Any, T2 : Any, R : Any> createComposed(
        keyDefault1: Pair<String, T1>,
        keyDefault2: Pair<String, T2>,
        serialize: (R) -> Pair<T1, T2>,
        deserialize: (T1, T2) -> R,
    ): Preference<R> {
        return prefs.composedPreference2(keyDefault1, keyDefault2,  serialize, deserialize)
    }

    fun <T : Any> create(key: String, default: T): Preference<T> {
        return prefs.preference(key, default)
    }

    fun <T : Any> create(@StringRes keyRes: Int, default: T): Preference<T> {
        return prefs.preference(context.getString(keyRes), default)
    }

    private fun <T : Any> SharedPreferences.preference(
        key: String,
        default: T
    ): Preference<T> {
        return PreferenceImpl(this, key, default)
    }

    private fun <T1 : Any, T2 : Any, R : Any> SharedPreferences.composedPreference2(
        keyDefault1: Pair<String, T1>,
        keyDefault2: Pair<String, T2>,
        serialize: (R) -> Pair<T1, T2>,
        deserialize: (T1, T2) -> R,
    ): Preference<R> {
        val pref1 = preference(keyDefault1.first, keyDefault1.second)
        val pref2 = preference(keyDefault2.first, keyDefault2.second)
        return ComposedPreference2Impl(
            pref1 = pref1,
            pref2 = pref2,
            default1 = keyDefault1.second,
            default2 = keyDefault2.second,
            serialize = serialize,
            deserialize = deserialize
        )
    }

}

private class PreferenceImpl<T : Any>(
    private val preferences: SharedPreferences,
    private val key: String,
    private val default: T,
) : Preference<T> {

    override fun get(): T {
        return getItem(key, default)
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(value: T) {
        preferences.edit(commit = true) {
            when (value) {
//                is IntEnum -> putInt(key, value.intValue)
//                is Enum<*> -> error("use ${IntEnum::class.qualifiedName}") TODO
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                is Set<*> -> putStringSet(key, value as Set<String>)
                is Duration -> putLong(key, value.inWholeMilliseconds)
            }
        }
    }

    override fun observe(): Flow<T> {
        val flow = MutableStateFlow(getItem(key, default))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                flow.value = getItem(key, default)
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        return flow.onCompletion {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun reset() {
        set(default)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getItem(key: String, default: T): T {
        return when (default) {
//            is IntEnum -> { TODO
//                val serialized = preferences.getInt(key, default.intValue)
//                default.companion.fromIntValue(serialized) as T
//            }
//            is Enum<*> -> error("use ${IntEnum::class.qualifiedName}, key=$key")
            is Duration -> {
                val serialized = preferences.getLong(key, default.inWholeMilliseconds)
                Duration.milliseconds(serialized) as T
            }
            else -> (preferences.all[key] ?: default) as T
        }
    }

}

private class ComposedPreference2Impl<T1 : Any, T2 : Any, R : Any>(
    private val pref1: Preference<T1>,
    private val pref2: Preference<T2>,
    private val default1: T1,
    private val default2: T2,
    private val serialize: (R) -> Pair<T1, T2>,
    private val deserialize: (T1, T2) -> R,
) : Preference<R> {

    override fun get(): R {
        return deserialize(pref1.get(), pref2.get())
    }

    override fun set(value: R) {
        val (t1, t2) = serialize(value)
        pref1.set(t1)
        pref2.set(t2)
    }

    override fun observe(): Flow<R> {
        return pref1.observe().combine(pref2.observe()) { t1, t2 ->
            deserialize(t1, t2)
        }
    }

    override fun reset() {
        pref1.set(default1)
        pref2.set(default2)
    }
}