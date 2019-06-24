package dev.olog.msc.domain.interactor.dialog

import dev.olog.core.MediaId
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.gateway.PodcastPlaylistGateway2
import dev.olog.core.interactor.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class RenameUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val playlistGateway: PlaylistGateway2,
    private val podcastPlaylistGateway: PodcastPlaylistGateway2

) : CompletableUseCaseWithParam<Pair<MediaId, String>>(scheduler) {


    override fun buildUseCaseObservable(param: Pair<MediaId, String>): Completable {
        val (mediaId, newTitle) = param
        return when {
            mediaId.isPodcastPlaylist -> podcastPlaylistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            mediaId.isPlaylist -> playlistGateway.renamePlaylist(mediaId.categoryValue.toLong(), newTitle)
            else -> Completable.error(IllegalArgumentException("not a folder nor a playlist, $mediaId"))
        }
    }
}