package dev.olog.core.interactor

import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlaylistGateway
import dev.olog.core.gateway.PodcastPlaylistGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.entity.PlaylistType
import io.reactivex.Completable
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val podcastPlaylistGateway: PodcastPlaylistGateway

): CompletableUseCaseWithParam<InsertCustomTrackListRequest>(scheduler) {

    override fun buildUseCaseObservable(param: InsertCustomTrackListRequest): Completable {
        if (param.type == PlaylistType.PODCAST){
            return podcastPlaylistGateway.createPlaylist(param.playlistTitle)
                    .flatMapCompletable { podcastPlaylistGateway.addSongsToPlaylist(it, param.tracksId) }
        }

        return playlistGateway.createPlaylist(param.playlistTitle)
                .flatMapCompletable { playlistGateway.addSongsToPlaylist(it, param.tracksId) }
    }
}

data class InsertCustomTrackListRequest(
        val playlistTitle: String,
        val tracksId: List<Long>,
        val type: PlaylistType
)