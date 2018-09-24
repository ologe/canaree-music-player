package dev.olog.msc.domain.interactor.playlist

import dev.olog.msc.domain.entity.PlaylistType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlaylistGateway

): CompletableUseCaseWithParam<InsertCustomTrackListRequest>(scheduler) {

    override fun buildUseCaseObservable(param: InsertCustomTrackListRequest): Completable {
        if (param.type == PlaylistType.PODCAST){
            return gateway.createPodcastPlaylist(param.playlistTitle)
                    .flatMapCompletable { gateway.addSongsToPodcastPlaylist(it, param.tracksId) }
        }

        return gateway.createPlaylist(param.playlistTitle)
                .flatMapCompletable { gateway.addSongsToPlaylist(it, param.tracksId) }
    }
}

data class InsertCustomTrackListRequest(
        val playlistTitle: String,
        val tracksId: List<Long>,
        val type: PlaylistType
)