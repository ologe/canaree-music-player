package dev.olog.data.migrations

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject

class BlacklistMigration @Inject constructor(
    private val preferences: SharedPreferences,
) {

    companion object {
        private const val BLACKLIST_KEY = "AppPreferencesDataStoreImpl.BLACKLIST"
    }

    fun migrate(db: SupportSQLiteDatabase) {
        val legacyBlacklist = getLegacyBlackList()
        if (legacyBlacklist.isNotEmpty()) {
            val blacklistValues = legacyBlacklist
                .joinToString(
                    separator = ",",
                    postfix = ";",
                    transform = { "('$it')" }
                )
            db.execSQL("INSERT INTO blacklist(directory) VALUES $blacklistValues")

            deleteLegacyBlacklist()
        }
    }

    private fun getLegacyBlackList(): Set<String> {
        return preferences.getStringSet(BLACKLIST_KEY, setOf())!!
    }

    private fun deleteLegacyBlacklist() {
        preferences.edit(commit = true) {
            remove(BLACKLIST_KEY)
        }
    }

}