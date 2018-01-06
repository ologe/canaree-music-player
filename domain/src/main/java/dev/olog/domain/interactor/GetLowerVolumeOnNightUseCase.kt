package dev.olog.domain.interactor

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.Flowable
import javax.inject.Inject

class GetLowerVolumeOnNightUseCase @Inject constructor(
        private val scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) {

    fun observe(): Flowable<Boolean> {
        return gateway.observeLowerVolumeOnNight()
                .subscribeOn(scheduler.worker)
                .observeOn(scheduler.ui)
    }

    fun get(): Boolean {
        return gateway.getLowerVolumeOnNight()
    }
}