package dev.olog.core.interactor.lastfm

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) {

    operator fun invoke(param: UserCredentials){
        val user = encryptUser(param)
        gateway.setLastFmCredentials(user)
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.encrypt(user.username),
            lastFmEncrypter.encrypt(user.password)
        )
    }

}