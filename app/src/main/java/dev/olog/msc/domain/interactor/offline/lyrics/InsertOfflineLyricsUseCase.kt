package dev.olog.msc.domain.interactor.offline.lyrics

import dev.olog.msc.domain.entity.OfflineLyrics
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.OfflineLyricsGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
        executors: IoScheduler,
        private val gateway: OfflineLyricsGateway

) : CompletableUseCaseWithParam<OfflineLyrics>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(offlineLyrics: OfflineLyrics): Completable {
        return gateway.saveLyrics(offlineLyrics)
    }
}