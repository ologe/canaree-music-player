package dev.olog.msc.domain.interactor.last.fm.scrobble

import dev.olog.core.entity.UserCredentials
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: LastFmEncrypter

) : ObservableUseCase<UserCredentials>(schedulers) {

    override fun buildUseCaseObservable(): Observable<UserCredentials> {
        return gateway.observeLastFmCredentials()
                .map { decryptUser(it) }
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}