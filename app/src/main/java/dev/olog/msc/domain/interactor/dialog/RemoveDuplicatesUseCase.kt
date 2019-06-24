package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.MediaId
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.core.interactor.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val playlistGateway: PlaylistGateway2,
    private val podcastPlaylistGateway: PodcastPlaylistGateway2

): CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        val playlistId = mediaId.resolveId
        if (mediaId.isPodcastPlaylist){
            return podcastPlaylistGateway.removeDuplicated(playlistId)
        }
        return playlistGateway.removeDuplicated(playlistId)
    }
}