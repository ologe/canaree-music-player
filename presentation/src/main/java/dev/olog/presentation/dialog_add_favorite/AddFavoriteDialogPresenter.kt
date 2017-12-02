package dev.olog.presentation.dialog_add_favorite

import dev.olog.domain.interactor.dialog.AddToFavoriteUseCase
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
        private val mediaId: String,
        private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute(): Completable {
        return addToFavoriteUseCase.execute(mediaId).timeout(5, TimeUnit.SECONDS)
    }

}