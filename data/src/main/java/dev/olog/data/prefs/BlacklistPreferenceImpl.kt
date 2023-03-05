package dev.olog.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.shared.android.utils.assertBackgroundThread
import javax.inject.Inject

class BlacklistPreferenceImpl @Inject constructor(
    private val preferences: SharedPreferences
) : BlacklistPreferences {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val BLACKLIST = "$TAG.BLACKLIST"
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