package dev.olog.presentation.dialogs.favorite

import dev.olog.core.MediaId
import dev.olog.msc.domain.interactor.dialog.AddToFavoriteUseCase
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute() {
        TODO()
//        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
//        return addToFavoriteUseCase(AddToFavoriteUseCase.Input(mediaId, type))
    }

}