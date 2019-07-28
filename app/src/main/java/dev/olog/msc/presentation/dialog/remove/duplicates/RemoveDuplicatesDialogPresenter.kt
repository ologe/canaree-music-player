package dev.olog.msc.presentation.dialog.remove.duplicates

import dev.olog.msc.domain.interactor.dialog.RemoveDuplicatesUseCase
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class RemoveDuplicatesDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val useCase: RemoveDuplicatesUseCase
) {

    fun execute(): Completable {
        TODO()
//        return useCase(mediaId)
    }

}