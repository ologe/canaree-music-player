package dev.olog.domain.interactor.music_service

import dev.olog.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class CurrentIdInPlaylistUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway

) : PrefsUseCase<Int>() {

    override fun get() = gateway.getCurrentIdInPlaylist()

    override fun set(param: Int) {
        gateway.setCurrentIdInPlaylist(param)
    }
}
