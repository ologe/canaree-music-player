package dev.olog.domain.interactor.service

import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class RepeatModeUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway

) : PrefsUseCase<Int>() {

    override fun get() = gateway.getRepeatMode()

    override fun set(param: Int) {
        gateway.setRepeatMode(param)
    }
}
