package dev.olog.presentation.dialogs.playlist.clear

import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.ClearPlaylistUseCase
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
    private val useCase: ClearPlaylistUseCase,
    private val schedulers: Schedulers

) {

    suspend fun execute(mediaId: MediaId) = withContext(schedulers.io) {
        useCase(mediaId)
    }

}