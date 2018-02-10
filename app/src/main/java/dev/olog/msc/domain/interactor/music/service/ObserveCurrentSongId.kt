package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class ObserveCurrentSongId @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: MusicPreferencesGateway

) : FlowableUseCase<Int>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<Int> {
        return gateway.observeCurrentIdInPlaylist()
    }
}
