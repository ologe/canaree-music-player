package dev.olog.msc.domain.interactor.playing.queue

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val playlistGateway: PlaylistGateway

): CompletableUseCaseWithParam<Long>(schedulers) {

    override fun buildUseCaseObservable(param: Long): Completable {
        return playlistGateway.insertSongToHistory(param)
    }
}