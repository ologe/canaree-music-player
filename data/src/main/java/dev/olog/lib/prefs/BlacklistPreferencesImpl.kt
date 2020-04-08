package dev.olog.lib.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.domain.prefs.BlacklistPreferences
import javax.inject.Inject

internal class BlacklistPreferencesImpl @Inject constructor(
    private val preferences: SharedPreferences
) : BlacklistPreferences {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        internal const val BLACKLIST = "$TAG.BLACKLIST"
    }

    override fun getBlackList(): Set<String> {
        return preferences.getStringSet(BLACKLIST, setOf())!!
    }

    override fun setBlackList(set: Set<String>) {
        preferences.edit { putStringSet(BLACKLIST, set) }
    }

    override fun setDefault() {
        setBlackList(setOf())
    }

}