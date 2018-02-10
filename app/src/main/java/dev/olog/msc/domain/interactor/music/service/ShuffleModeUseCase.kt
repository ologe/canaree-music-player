package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class ShuffleModeUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway

) : PrefsUseCase<Int>() {

    override fun get() = gateway.getShuffleMode()

    override fun set(param: Int) {
        gateway.setShuffleMode(param)
    }
}
