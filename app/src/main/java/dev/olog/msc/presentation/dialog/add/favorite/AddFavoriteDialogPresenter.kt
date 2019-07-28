package dev.olog.msc.presentation.dialog.add.favorite

import dev.olog.core.MediaId
import dev.olog.msc.domain.interactor.dialog.AddToFavoriteUseCase
import io.reactivex.Completable
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute(): Completable {
        TODO()
//        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
//        return addToFavoriteUseCase(AddToFavoriteUseCase.Input(mediaId, type))
    }

}