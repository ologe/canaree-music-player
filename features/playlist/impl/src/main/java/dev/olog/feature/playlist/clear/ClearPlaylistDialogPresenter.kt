package dev.olog.feature.playlist.clear

import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.ClearPlaylistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
    private val useCase: ClearPlaylistUseCase

) {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        useCase(mediaId)
    }

}