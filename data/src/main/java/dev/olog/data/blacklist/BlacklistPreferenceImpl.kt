package dev.olog.data.blacklist

import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.data.blacklist.db.BlacklistDao
import dev.olog.data.blacklist.db.BlacklistEntity
import javax.inject.Inject

class BlacklistPreferenceImpl @Inject constructor(
    private val dao: BlacklistDao,
) : BlacklistPreferences {

    override suspend fun getBlackList(): List<String> {
        return dao.getAll().map { it.directory }
    }

    override suspend fun setBlackList(items: List<String>) {
        dao.replaceAll(items.distinct().map { BlacklistEntity(it) })
    }

}