package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: PlaylistGateway

): CompletableUseCaseWithParam<Pair<Long, Long>>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(pair: Pair<Long, Long>): Completable {
        val (playlistId, idInPlaylist) = pair
        return gateway.removeFromPlaylist(playlistId, idInPlaylist)
    }
}