package dev.olog.data.blacklist

import dev.olog.core.gateway.BlacklistGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.BlacklistQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class BlacklistRepository @Inject constructor(
    private val queries: BlacklistQueries,
    private val schedulers: Schedulers,
) : BlacklistGateway {

    override fun observeBlacklist(): Flow<List<File>> {
        return queries.selectAll()
            .mapToFlowList(schedulers.io)
            .mapListItem { File(it) }
    }

    override suspend fun setBlacklist(directories: List<File>) = withContext(schedulers.io) {
        queries.transaction {
            queries.deleteAll()
            for (dir in directories) {
                queries.insert(dir.path)
            }
        }
    }
}