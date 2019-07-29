package dev.olog.presentation.dialogs.playlist.clear

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