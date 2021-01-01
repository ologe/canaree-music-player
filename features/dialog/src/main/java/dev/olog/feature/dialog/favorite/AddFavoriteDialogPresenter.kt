package dev.olog.feature.dialog.favorite

import dev.olog.domain.entity.Favorite
import dev.olog.domain.interactor.AddToFavoriteUseCase
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    suspend fun execute(mediaId: MediaId) {
        addToFavoriteUseCase(
            mediaId = mediaId,
            type = Favorite.Type.fromMediaId(mediaId)
        )
    }

}