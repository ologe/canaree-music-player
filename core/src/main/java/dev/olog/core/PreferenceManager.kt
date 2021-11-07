package dev.olog.core

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) {

    fun <T: Any> createEnum(
        key: String,
        default: T,
        serialize: (T) -> String,
        deserialize: (String) -> T,
    ): Preference<T> {
        return EnumPreferenceImpl(
            pref = prefs.preference(key, serialize(default)),
            serialize = serialize,
            deserialize = deserialize
        )
    }

    fun <T1 : Any, T2 : Any, R : Any> createComposed(
        keyDefault1: Pair<String, T1>,
        keyDefault2: Pair<String, T2>,
        serialize: (R) -> Pair<T1, T2>,
        deserialize: (T1, T2) -> R,
    ): Preference<R> {
        val pref1 = prefs.preference(keyDefault1.first, keyDefault1.second)
        val pref2 = prefs.preference(keyDefault2.first, keyDefault2.second)
        return ComposedPreference2Impl(
            pref1 = pref1,
            pref2 = pref2,
            default1 = keyDefault1.second,
            default2 = keyDefault2.second,
            serialize = serialize,
            deserialize = deserialize
        )
    }

    fun <T : Any> create(key: String, default: T): Preference<T> {
        return PreferenceImpl(
            preferences = prefs,
            key = key,
            default = default
        )
    }

    fun <T : Any> create(@StringRes keyRes: Int, default: T): Preference<T> {
        return create(
            key = context.getString(keyRes),
            default = default
        )
    }

    private fun <T : Any> SharedPreferences.preference(
        key: String,
        default: T
    ): Preference<T> {
        return PreferenceImpl(
            preferences = this,
            key = key,
            default = default
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

private class EnumPreferenceImpl<T : Any>(
    private val pref: Preference<String>,
    private val serialize: (T) -> String,
    private val deserialize: (String) -> T,
) : Preference<T> {

    override fun get(): T = deserialize(pref.get())

    override fun set(value: T) {
        pref.set(serialize(value))
    }

    override fun observe(): Flow<T> = pref.observe().map { deserialize(it) }

    override fun reset() {
        pref.reset()
    }
}