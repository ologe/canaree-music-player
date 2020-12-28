package dev.olog.core.prefs

import dev.olog.core.ResettablePreference

interface BlacklistPreferences : ResettablePreference {
    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)
}