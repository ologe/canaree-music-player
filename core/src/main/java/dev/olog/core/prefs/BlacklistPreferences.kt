package dev.olog.core.prefs

import dev.olog.core.Resettable

interface BlacklistPreferences : Resettable {
    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)
}