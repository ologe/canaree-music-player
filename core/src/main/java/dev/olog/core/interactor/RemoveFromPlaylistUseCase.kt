package dev.olog.core.interactor

import dev.olog.core.entity.PlaylistType
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlaylistGateway
import dev.olog.core.gateway.PodcastPlaylistGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway,
        private val podcastGateway: PodcastPlaylistGateway

): CompletableUseCaseWithParam<RemoveFromPlaylistUseCase.Input>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(input: Input): Completable {
        if (input.type == PlaylistType.PODCAST){
            return podcastGateway.removeSongFromPlaylist(input.playlistId, input.idInPlaylist)
        }
        return playlistGateway.removeFromPlaylist(input.playlistId, input.idInPlaylist)
    }

    class Input(
            val playlistId: Long,
            val idInPlaylist: Long,
            val type: PlaylistType
    )

}