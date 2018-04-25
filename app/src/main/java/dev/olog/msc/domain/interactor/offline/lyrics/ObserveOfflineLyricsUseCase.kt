package dev.olog.msc.domain.interactor.offline.lyrics

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.OfflineLyricsGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import io.reactivex.Observable
import javax.inject.Inject

class ObserveOfflineLyricsUseCase @Inject constructor(
        executors: IoScheduler,
        private val gateway: OfflineLyricsGateway

) : ObservableUseCaseUseCaseWithParam<String, Long>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(id: Long): Observable<String> {
        return gateway.observeLyrics(id)
    }
}