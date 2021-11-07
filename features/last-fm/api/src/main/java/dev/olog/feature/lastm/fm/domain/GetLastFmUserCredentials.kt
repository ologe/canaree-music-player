package dev.olog.feature.lastm.fm.domain

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.feature.lastm.fm.LastFmPrefs
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
    private val prefs: LastFmPrefs,
    private val lastFmEncrypter: IEncrypter

) {

    fun execute(): UserCredentials {
        return decryptUser(prefs.credentials.get())
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}