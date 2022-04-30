package dev.olog.shared

import android.content.SharedPreferences

/**
 * In memory implementation of [SharedPreferences]
 */
class TestSharedPreferences : SharedPreferences {

    val cache = mutableMapOf<String, Any?>()
    private val editor = Editor(cache, this)
    val listeners: List<SharedPreferences.OnSharedPreferenceChangeListener>
        get() = editor.listeners

    override fun getAll(): MutableMap<String, *> = cache

    override fun getString(key: String, defValue: String?): String? {
        return get(key, defValue)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? {
        return get(key, defValues)
    }

    override fun getInt(key: String, defValue: Int): Int {
        return get(key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return get(key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return get(key, defValue)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return get(key, defValue)
    }

    inline fun <reified T> get(key: String, defValue: T): T {
        return cache[key] as T? ?: defValue
    }

    override fun contains(key: String): Boolean {
        return key in cache
    }

    override fun edit(): SharedPreferences.Editor = editor

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        editor.listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        editor.listeners.remove(listener)
    }

    class Editor(
        private val map: MutableMap<String, Any?>,
        private val preferences: SharedPreferences,
        val listeners: MutableList<SharedPreferences.OnSharedPreferenceChangeListener> = mutableListOf(),
    ) : SharedPreferences.Editor {

        private val cache = mutableMapOf<String, Any?>()

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            cache[key] = value
            return this
        }

        override fun putStringSet(
            key: String,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            cache[key] = values
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            cache[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            cache[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            cache[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            cache[key] = value
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            cache[key] = null
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            error("not supported")
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            for ((k, v) in cache) {
                map[k] = v
                for (listener in listeners) {
                    listener.onSharedPreferenceChanged(preferences, k)
                }
            }
            cache.clear()
        }
    }

}