package dev.olog.domain.interactor.lastfm

import dev.olog.domain.IEncrypter
import dev.olog.domain.entity.UserCredentials
import dev.olog.domain.prefs.AppPreferencesGateway
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) {

    operator fun invoke(): UserCredentials {
        return decryptUser(gateway.getLastFmCredentials())
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            username = lastFmEncrypter.decrypt(user.username),
            password = lastFmEncrypter.decrypt(user.password)
        )
    }

}