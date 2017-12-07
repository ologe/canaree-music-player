package dev.olog.domain.interactor.music_service

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
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