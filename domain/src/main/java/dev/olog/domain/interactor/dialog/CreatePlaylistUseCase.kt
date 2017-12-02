package dev.olog.domain.interactor.dialog

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val playlistGateway: PlaylistGateway

) : SingleUseCaseWithParam<Long, String>(scheduler) {

    override fun buildUseCaseObservable(param: String): Single<Long> {
        return playlistGateway.createPlaylist(param)
    }
}