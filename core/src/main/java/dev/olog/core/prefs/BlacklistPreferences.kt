package dev.olog.core.prefs

interface BlacklistPreferences {
    suspend fun getBlackList(): List<String>
    suspend fun setBlackList(items: List<String>)
}