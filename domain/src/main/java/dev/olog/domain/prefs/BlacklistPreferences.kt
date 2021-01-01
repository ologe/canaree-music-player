package dev.olog.domain.prefs

import dev.olog.domain.ResettablePreference

interface BlacklistPreferences : ResettablePreference {
    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)
}