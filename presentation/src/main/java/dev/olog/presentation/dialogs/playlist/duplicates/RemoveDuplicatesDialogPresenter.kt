package dev.olog.presentation.dialogs.playlist.duplicates

import dev.olog.core.MediaId
import dev.olog.msc.domain.interactor.dialog.RemoveDuplicatesUseCase
import javax.inject.Inject

class RemoveDuplicatesDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val useCase: RemoveDuplicatesUseCase
) {

    fun execute() {
        TODO()
//        return useCase(mediaId)
    }

}