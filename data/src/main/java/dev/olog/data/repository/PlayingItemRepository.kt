package dev.olog.data.repository

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingItemGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.PlayingItemQueries
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.playable.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PlayingItemRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: PlayingItemQueries
) : PlayingItemGateway {

    override suspend fun set(id: Long) = withContext(schedulers.io) {
        queries.replace(id)
    }

    override fun get(): Song? {
        return queries.select()
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observe(): Flow<Song?> {
        return queries.select()
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

}

internal fun PlayingItemQueries.replace(playable_id: Long) = transaction {
    delete()
    insert(playable_id)
}