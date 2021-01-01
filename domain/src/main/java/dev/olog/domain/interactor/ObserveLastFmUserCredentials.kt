package dev.olog.domain.interactor

import dev.olog.domain.IEncrypter
import dev.olog.domain.entity.UserCredentials
import dev.olog.domain.interactor.base.FlowUseCase
import dev.olog.domain.prefs.AppPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) : FlowUseCase<UserCredentials>() {

    override fun buildUseCase(): Flow<UserCredentials> {
        return gateway.observeLastFmCredentials()
            .map { decryptUser(it) }
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}