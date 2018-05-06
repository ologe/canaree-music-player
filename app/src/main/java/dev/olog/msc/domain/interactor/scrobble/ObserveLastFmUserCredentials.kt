package dev.olog.msc.domain.interactor.scrobble

import dev.olog.msc.domain.entity.UserCredendials
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway,
        private val lastFmEncrypter: LastFmEncrypter

) : ObservableUseCase<UserCredendials>(schedulers) {

    override fun buildUseCaseObservable(): Observable<UserCredendials> {
        return gateway.observeLastFmCredentials()
                .map { decryptUser(it) }
    }

    private fun decryptUser(user: UserCredendials): UserCredendials {
        return UserCredendials(
                lastFmEncrypter.decrypt(user.username),
                lastFmEncrypter.decrypt(user.password)
        )
    }

}