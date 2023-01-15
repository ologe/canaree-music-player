package dev.olog.data.blacklist

import dev.olog.core.gateway.BlacklistGateway
import javax.inject.Inject

class BlacklistRepository @Inject constructor(
    private val dao: BlacklistDao
) : BlacklistGateway {

    override suspend fun getBlacklist(): List<String> {
        return dao.getAll().map { it.directory }
    }

    override suspend fun setBlacklist(items: List<String>) {
        dao.replaceAll(items.map { BlacklistEntity(it) })
    }

    override suspend fun reset() {
        dao.deleteAll()
    }
}