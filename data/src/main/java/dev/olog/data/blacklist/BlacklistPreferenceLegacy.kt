package dev.olog.data.blacklist

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.Migration
import javax.inject.Inject

@Migration
class BlacklistPreferenceLegacy @Inject constructor(
    private val preferences: SharedPreferences,
) {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        const val BLACKLIST_KEY = "$TAG.BLACKLIST"
    }

    fun getBlackList(): Set<String> {
        return preferences.getStringSet(BLACKLIST_KEY, setOf())!!
    }

    fun delete() {
        preferences.edit { remove(BLACKLIST_KEY) }
    }

}