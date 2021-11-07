package dev.olog.feature.lastm.fm.domain

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.feature.lastm.fm.LastFmPrefs
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
    private val prefs: LastFmPrefs,
    private val lastFmEncrypter: IEncrypter

) {

    operator fun invoke(param: UserCredentials){
        val user = encryptUser(param)
        prefs.credentials.set(user)
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.encrypt(user.username),
            lastFmEncrypter.encrypt(user.password)
        )
    }

}