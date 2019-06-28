package dev.olog.msc.domain.interactor.last.fm.scrobble

import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.injection.EncrypterImpl
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: EncrypterImpl

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