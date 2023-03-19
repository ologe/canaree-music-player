package dev.olog.data.blacklist

import dev.olog.core.gateway.BlacklistGateway
import javax.inject.Inject

class BlacklistRepository @Inject constructor(
    private val dao: BlacklistDao,
) : BlacklistGateway {

    override suspend fun getAll(): List<String> {
        return dao.getAll().map { it.directory }
    }

    // TODO ensure all write uses relative path
    override suspend fun setAll(items: List<String>) {
        dao.replaceAll(items.distinct().map { BlacklistEntity(it) })
    }

}