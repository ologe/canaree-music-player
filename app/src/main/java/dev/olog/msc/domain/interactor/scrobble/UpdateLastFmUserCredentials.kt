package dev.olog.msc.domain.interactor.scrobble

import dev.olog.msc.domain.entity.UserCredendials
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway,
        private val lastFmEncrypter: LastFmEncrypter

) : CompletableUseCaseWithParam<UserCredendials>(schedulers) {

    override fun buildUseCaseObservable(param: UserCredendials): Completable {
        return Completable.create {
            val user = encryptUser(param)
            gateway.setLastFmCredentials(user)

            it.onComplete()
        }
    }

    private fun encryptUser(user: UserCredendials): UserCredendials {
        return UserCredendials(
                lastFmEncrypter.encrypt(user.username),
                lastFmEncrypter.encrypt(user.password)
        )
    }

}