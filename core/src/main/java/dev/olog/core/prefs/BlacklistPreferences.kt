package dev.olog.core.prefs

import io.reactivex.Completable

interface BlacklistPreferences {
    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)

    fun setDefault(): Completable
}