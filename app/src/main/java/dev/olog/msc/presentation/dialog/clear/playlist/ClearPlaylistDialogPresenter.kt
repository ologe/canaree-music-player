package dev.olog.msc.presentation.dialog.clear.playlist

import dev.olog.core.MediaId
import dev.olog.msc.domain.interactor.dialog.ClearPlaylistUseCase
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val useCase: ClearPlaylistUseCase

) {

    fun execute() {
        TODO()
//        return useCase(mediaId)
    }

}