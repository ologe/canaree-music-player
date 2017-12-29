package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class ClearPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        val playlistId = mediaId.categoryValue.toLong()
        return playlistGateway.clearPlaylist(playlistId)
    }
}