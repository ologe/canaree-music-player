package dev.olog.core

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) {

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