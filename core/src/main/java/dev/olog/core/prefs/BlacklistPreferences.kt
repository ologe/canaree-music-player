package dev.olog.core.prefs

import dev.olog.core.Prefs

interface BlacklistPreferences : Prefs {
    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)

    fun setDefault()
}