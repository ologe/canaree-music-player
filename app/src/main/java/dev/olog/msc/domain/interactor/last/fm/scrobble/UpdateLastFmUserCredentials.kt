package dev.olog.msc.domain.interactor.last.fm.scrobble

import dev.olog.core.entity.UserCredentials
import dev.olog.core.executor.IoScheduler
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.interactor.CompletableUseCaseWithParam
import dev.olog.injection.LastFmEncrypter
import io.reactivex.Completable
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: LastFmEncrypter

) : CompletableUseCaseWithParam<UserCredentials>(schedulers) {

    override fun buildUseCaseObservable(param: UserCredentials): Completable {
        return Completable.create {
            val user = encryptUser(param)
            gateway.setLastFmCredentials(user)

            it.onComplete()
        }
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.encrypt(user.username),
            lastFmEncrypter.encrypt(user.password)
        )
    }

}