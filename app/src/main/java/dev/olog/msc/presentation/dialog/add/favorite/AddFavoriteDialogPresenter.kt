package dev.olog.msc.presentation.dialog.add.favorite

import dev.olog.msc.domain.interactor.dialog.AddToFavoriteUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute(): Completable {
        return addToFavoriteUseCase.execute(mediaId)
    }

}