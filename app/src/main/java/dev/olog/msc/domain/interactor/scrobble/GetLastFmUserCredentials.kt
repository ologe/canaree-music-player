package dev.olog.msc.domain.interactor.scrobble

import dev.olog.msc.domain.entity.UserCredendials
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
        private val gateway: AppPreferencesGateway,
        private val lastFmEncrypter: LastFmEncrypter

) {

    fun execute(): UserCredendials {
        return decryptUser(gateway.getLastFmCredentials())
    }

    private fun decryptUser(user: UserCredendials): UserCredendials {
        return UserCredendials(
                lastFmEncrypter.decrypt(user.username),
                lastFmEncrypter.decrypt(user.password)
        )
    }

}