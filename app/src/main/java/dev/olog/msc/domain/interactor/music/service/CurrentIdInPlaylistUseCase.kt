package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.interactor.base.PrefsUseCase
import javax.inject.Inject

class CurrentIdInPlaylistUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway

) : PrefsUseCase<Int>() {

    override fun get() = gateway.getCurrentIdInPlaylist()

    override fun set(param: Int) {
        gateway.setCurrentIdInPlaylist(param)
    }
}
