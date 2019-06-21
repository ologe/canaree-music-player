package dev.olog.msc.presentation.dialog.clear.playlist

import dev.olog.msc.domain.interactor.dialog.ClearPlaylistUseCase
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val useCase: ClearPlaylistUseCase

) {

    fun execute(): Completable {
        return useCase.execute(mediaId)
    }

}