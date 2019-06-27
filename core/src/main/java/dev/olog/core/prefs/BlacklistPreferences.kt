package dev.olog.core.prefs

interface BlacklistPreferences {
    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)

    fun setDefault()
}