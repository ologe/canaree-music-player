package dev.olog.msc.presentation.dialog.delete

import dev.olog.msc.domain.interactor.dialog.DeleteUseCase
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class DeleteDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val deleteUseCase: DeleteUseCase
) {


    fun execute(): Completable {
        return deleteUseCase.execute(mediaId)
    }

}