package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class RenamePlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway

) : CompletableUseCaseWithParam<Pair<Long, String>>(scheduler) {


    override fun buildUseCaseObservable(param: Pair<Long, String>): Completable {
        val (playlistId, newTitle) = param
        return playlistGateway.renamePlaylist(playlistId, newTitle)
    }
}