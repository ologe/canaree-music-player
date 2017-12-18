package dev.olog.domain.interactor.data

import dev.olog.domain.gateway.prefs.DataPreferencesGateway
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class PlaylistImagesUseCase @Inject constructor(
        private val dataPreferencesGateway: DataPreferencesGateway
) {

    fun areImagesCreated(): AtomicBoolean {
        return dataPreferencesGateway.arePlaylistImagesCreated()
    }

    fun setCreated(){
        dataPreferencesGateway.setPlaylistImagesCreated()
    }

}