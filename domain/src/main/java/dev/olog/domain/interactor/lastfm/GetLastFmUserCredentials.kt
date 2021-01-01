package dev.olog.domain.interactor.lastfm

import dev.olog.domain.IEncrypter
import dev.olog.domain.entity.UserCredentials
import dev.olog.domain.prefs.AppPreferencesGateway
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
    private val prefs: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) {

    fun execute(): UserCredentials {
        return decryptUser(prefs.getLastFmCredentials())
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}