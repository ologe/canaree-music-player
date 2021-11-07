package dev.olog.feature.lastm.fm.domain

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.interactor.base.FlowUseCase
import dev.olog.feature.lastm.fm.LastFmPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
    private val prefs: LastFmPrefs,
    private val lastFmEncrypter: IEncrypter

) : FlowUseCase<UserCredentials>() {

    override fun buildUseCase(): Flow<UserCredentials> {
        return prefs.credentials.observe()
            .map { decryptUser(it) }
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}