package dev.olog.msc.presentation.dialog.add.favorite

import dev.olog.msc.domain.entity.FavoriteType
import dev.olog.msc.domain.interactor.dialog.AddToFavoriteUseCase
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute(): Completable {
        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        return addToFavoriteUseCase.execute(AddToFavoriteUseCase.Input(mediaId, type))
    }

}