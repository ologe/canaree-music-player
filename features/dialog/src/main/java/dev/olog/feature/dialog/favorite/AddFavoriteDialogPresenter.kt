package dev.olog.feature.dialog.favorite

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.favorite.FavoriteType
import dev.olog.domain.interactor.AddToFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
    private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO) {
        val type = if (mediaId.isAnyPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        addToFavoriteUseCase(AddToFavoriteUseCase.Input(mediaId, type))
    }

}