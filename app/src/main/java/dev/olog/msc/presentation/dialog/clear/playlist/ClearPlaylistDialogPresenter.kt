package dev.olog.msc.presentation.dialog.clear.playlist

import dev.olog.msc.domain.interactor.dialog.ClearPlaylistUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val clearPlaylistUseCase: ClearPlaylistUseCase

) {

    fun execute(): Completable {
        return clearPlaylistUseCase.execute(mediaId)
    }

}