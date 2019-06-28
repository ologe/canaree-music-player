package dev.olog.core.interactor

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.executor.IoScheduler
import dev.olog.core.interactor.base.ObservableUseCase
import dev.olog.core.prefs.AppPreferencesGateway
import io.reactivex.Observable
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

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