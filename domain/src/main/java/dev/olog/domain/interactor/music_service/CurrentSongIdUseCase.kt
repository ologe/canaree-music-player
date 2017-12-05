package dev.olog.domain.interactor.music_service

import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class CurrentSongIdUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway

) : PrefsUseCase<Long>() {

    override fun get() = gateway.getCurrentSongId()

    override fun set(param: Long) {
        gateway.setCurrentSongId(param)
    }
}
