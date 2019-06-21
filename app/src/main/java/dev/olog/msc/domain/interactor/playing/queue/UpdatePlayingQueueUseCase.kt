package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class UpdatePlayingQueueUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: PlayingQueueGateway

) : CompletableUseCaseWithParam<List<UpdatePlayingQueueUseCaseRequest>>(schedulers) {

    override fun buildUseCaseObservable(param: List<UpdatePlayingQueueUseCaseRequest>): Completable {
        return gateway.update(param)
    }

}

data class UpdatePlayingQueueUseCaseRequest(
    val mediaId: MediaId,
    val songId: Long,
    val idInPlaylist: Int
)