package dev.olog.domain.interactor.prefs

import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class BlackListUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway

) : PrefsUseCase<Set<String>>() {

    override fun get(): Set<String> {
        return gateway.getBlackList()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun set(blackList: Set<String>) {
        return gateway.setBlackList(blackList)
    }
}