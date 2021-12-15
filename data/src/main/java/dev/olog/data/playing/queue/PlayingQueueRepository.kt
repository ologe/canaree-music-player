package dev.olog.data.playing.queue

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.PlayingQueueQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.playingQueue.SelectAll
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val permissionManager: PermissionManager,
    private val queries: PlayingQueueQueries,
    private val songGateway: SongGateway,
) : PlayingQueueGateway {

    override fun getAll(): List<PlayingQueueSong> {
        if (permissionManager.hasPermission(Permission.Storage)) {
            return queries.selectAll()
                .executeAsList()
                .map(SelectAll::toDomain)
                .takeIf { it.isNotEmpty() }
                ?: songGateway.getAll()
                    .mapIndexed { index, song -> PlayingQueueSong(song, index) }
        }
        return emptyList()
    }

    override fun observeAll(): Flow<List<PlayingQueueSong>> = flow {
        permissionManager.awaitPermission(Permission.Storage)

        val flow = queries.selectAll()
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectAll::toDomain)

        emitAll(flow)
    }

    override suspend fun update(list: List<UpdatePlayingQueueUseCase.Request>) = withContext(schedulers.io) {
        queries.transaction {
            queries.deleteAll()
            list.forEach {
                queries.insert(
                    playable_id = it.playableId,
                    play_order = it.playOrder
                )
            }
        }
    }

}
