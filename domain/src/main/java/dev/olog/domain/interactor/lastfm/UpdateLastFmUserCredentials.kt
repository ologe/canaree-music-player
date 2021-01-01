package dev.olog.domain.interactor.lastfm

import dev.olog.domain.IEncrypter
import dev.olog.domain.entity.UserCredentials
import dev.olog.domain.prefs.AppPreferencesGateway
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
    private val prefs: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) {

    operator fun invoke(param: UserCredentials){
        val user = encryptUser(param)
        prefs.setLastFmCredentials(user)
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.encrypt(user.username),
            lastFmEncrypter.encrypt(user.password)
        )
    }

}