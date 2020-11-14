package dev.olog.core.interactor.lastfm

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) {

    fun execute(): UserCredentials {
        return decryptUser(gateway.getLastFmCredentials())
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}