package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
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