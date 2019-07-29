package dev.olog.presentation.dialogs.delete

import dev.olog.core.MediaId
import dev.olog.msc.domain.interactor.dialog.DeleteUseCase
import javax.inject.Inject

class DeleteDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val deleteUseCase: DeleteUseCase
) {


    fun execute() {
        TODO()
//        return deleteUseCase(mediaId)
    }

}