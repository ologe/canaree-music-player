package dev.olog.domain.interactor.music_service

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCase
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
